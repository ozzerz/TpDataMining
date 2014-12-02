#!/usr/bin/env python2.5
# encoding: utf-8
"""
cleaneval.py v1.0

Simple and fast evaluation of CleanEval-1 tasks (precision, recall, F-score).
"""

import sys
import os
import string
import re
from math import *
from difflib import SequenceMatcher
from getopt import getopt, GetoptError
from time import time
import datetime

t1 = time()

help_message = '''
This is cleaneval.py version 1.0 -- Copyright (C) 2008 by Stefan Evert

Usage:  cleaneval.py [options] <texts_dir> <gold_dir> [<output_dir>]

Options:
  -t    print total precision/recall for all files (micro-averaged)
        (does not print results for individual files)
  -n    omit table header (e.g. to combine multiple tables)
  -s    calculate summary statistics (mean / s.d.) and print
  -a    remove non-ASCII characters before comparison
  -u    calculate unlabelled segmentation accuracy
  -x    normalize <texts_dir> whith shallow option
        ps: whithout this option the normalize process will be
        in deep mod.

Evaluates automatically cleaned files in directory <texts_dir> against
gold standard files in directory <gold_dir>.  Correspoding files in the 
two directories must have identical names and there must be no other files
in these directories.

The script prints a TAB-delimited evaluation table on standard output, which
can be redirected to a file and read into R, Excel or a similar application.
Precision, recall and F-score are calculated as percentages of whitespace-
delimited words.  Accuracy of segment identification is measured by precision,
recall and F-score for labelled or unlabelled segment marker tags (if the
option -u is specified, no distinction is made between types <p>, <h> and <l>).

If the third argument is given, full alignments will be written to separate
files in directory <output_dir>, and also gold and scrapped files after 
normalizing will be written in directory <output_dir>.
'''

re_URL = re.compile("^\s*URL.*$", re.MULTILINE)
re_TAG = re.compile("(<[phl]>)", re.IGNORECASE)
re_TAGMORE = re.compile("</?(a|i|b|img|em|span|td|th|tr|table|html|body|div|font|strong|head|link|meta|ul|li|blockquote|title|(h[123456789]))(>|\s.*?>)", re.IGNORECASE)
re_PART = re.compile("(<style.*>.*</style>)|(</?!.*>)|(<script.*>.*</script>)|(<base.*>.*</base>)", re.IGNORECASE)
# |script|base
re_TAGINLINE = re.compile("<(br|hr|noscript|meta|input)\s*/?\s*>", re.IGNORECASE)
re_MULTP = re.compile("(</?p(>|\s.*?>)\s*)+", re.DOTALL|re.IGNORECASE) # \s* Dotall: match any character at all, including a newline
re_WS = re.compile("\s+") ### attention re.UNICODE -> pb pour le chinois plusieurs caractères toujours présents re.UNICODE -> pb remplace pas que les espaces

re_CTRL = re.compile("[\x00-\x1F]+")
re_HI = re.compile("[\x80-\xFF]+")
# --------- a ajouter pour la normalisation ---------
# normaliser les espaces avant les '.', ',' ou ';' etc
# supprimer les codes Html, ex: &#8212
# supprimer les '|'
# supprimer le "parsing corpus" des fichiers de bp
# plusieurs espaces apres normalisation dans les .align chinois -> faux positifs


def slurp_file(filename):
    fh = file(filename)
    body = fh.read()
    fh.close()
    return body


def quadri(texte):
    '''Par Mr. Lejeune.'''
    l = []
    for i in xrange(0,len(texte)-4):
        mot = texte[i:i+4]
        l.append(mot)
    return l


def normalize(text, filename, last_dir="", ascii=False, unlabelled=False, scrap=False):    
    text = re_URL.sub("", text)           # remove URL line at start of gold standard files
    text = re_CTRL.sub(" ", text)         # replace any control characters by spaces (includes newlines)

    if unlabelled:
        text = re_TAG.sub("\n<p>  ", text) # start each segment on new line, normalise tags
    else:
        text = re_TAG.sub("\n\g<1> ", text) # only break lines before segment markers

    if ascii:
        text = re_HI.sub("", text) # delete non-ASCII characters (to avoid charset problems)
        
    
    text = re_TAGMORE.sub("\n<p> ", text)
    text = re_TAGINLINE.sub(" ", text)
    text = re_PART.sub("", text)

    text = re_MULTP.sub("\n<p> ", text) # remplacer les multiples <p>
    text = re_WS.sub(" ", text)           # normalise whitespace (including line breaks) to single spaces
    
    if output_dir:
        normalize_path = os.path.join(output_dir, last_dir)
        try:
            os.makedirs(normalize_path)
        except OSError:
            pass

        if scrap:
            normalize_file = os.path.join(normalize_path, os.path.splitext(filename)[0] + ".scrapped.normalize")
            try:
                f = open(normalize_file, "w")
                f.write(text)
                f.close()
            except IOError:
                print "[Error file writting in normalize]"		

        else:
            normalize_file = os.path.join(normalize_path, os.path.splitext(filename)[0] + ".gold.normalize")
            try:
                f = open(normalize_file, "w")
                f.write(text)
                f.close()
            except IOError:
                print "[Error file writting in normalize]"

    return text


## return diff as list of tuples ("equal"/"insert"/"delete", [text words], [gold words])
def make_diff(alignment, text_w, gold_w):
    diff = []
    for tag, i1, i2, j1, j2 in alignment.get_opcodes():
        text_region = text_w[i1:i2]
        gold_region = gold_w[j1:j2]
        if tag == "replace":
            diff.append( ("delete", text_region, []) )
            diff.append( ("insert", [], gold_region) )
        else:
            diff.append( (tag, text_region, gold_region) )
    return diff

## return evaluation measures for given diff:
##   (f-score, precision, recall, [labelled] segmentation f-score, precision, recall)
def evaluate(diff):
    tp = fp = fn = 0
    tag_tp = tag_fp = tag_fn = 0
    for tag, text, gold in diff:
        text_tags = len( filter(re_TAG.match, text) )
        gold_tags = len( filter(re_TAG.match, gold) )
        text_l = len(text)
        gold_l = len(gold)
        if tag == "delete":
            fp += text_l
            tag_fp += text_tags
        elif tag == "insert":
            fn += gold_l
            tag_fn += gold_tags
        else:
            tp += text_l
            tag_tp += text_tags
            assert text_l == gold_l
            assert text_tags == gold_tags

    n_text = tp + fp if tp + fp > 0 else 1
    n_gold = tp + fn if tp + fn > 0 else 1
    precision = float(tp) / n_text
    recall = float(tp) / n_gold
    precision_plus_recall = precision + recall if precision + recall > 0 else 1
    f_score = 2 * precision * recall / precision_plus_recall

    tags_text = tag_tp + tag_fp if tag_tp + tag_fp > 0 else 1
    tags_gold = tag_tp + tag_fn if tag_tp + tag_fn > 0 else 1
    tag_precision = float(tag_tp) / tags_text
    tag_recall = float(tag_tp) / tags_gold
    precision_plus_recall = tag_precision + tag_recall if tag_precision + tag_recall > 0 else 1
    tag_f_score = 2 * tag_precision * tag_recall / precision_plus_recall

    return (100 * f_score, 100 * precision, 100 * recall, 100 * tag_f_score, 100 * tag_precision, 100 * tag_recall, tp, fp, fn, tag_tp, tag_fp, tag_fn)

def write_alignment(diff, filename):
    fh = file(filename, "w")
    for tag, text_seg, gold_seg in diff:
        if tag == "delete":
            print >> fh, "<" * 40, "(false positive)"
            print >> fh, " ".join(text_seg)
        if tag == "insert":
            print >> fh, ">" * 40, "(false negative)"
            print >> fh, " ".join(gold_seg)
        if tag == "equal":
            print >> fh, "=" * 40
            print >> fh, " ".join(gold_seg)
    fh.close()

if __name__ == "__main__":
    # ---------------------------------------------------------------------------
    # --------------------------------- parser ----------------------------------
    # ---------------------------------------------------------------------------
    try:
        options, args = getopt(sys.argv[1:], "tnsaul")
    except GetoptError:
        print >> sys.stderr, help_message
        sys.exit(2)

    if len(args) not in (3,4):
        print >> sys.stderr, help_message
        sys.exit(2)

    opt_total = ('-t', '') in options
    opt_noheader = ('-n', '') in options
    opt_summary = ('-s', '') in options
    opt_unlabelled = ('-u', '') in options
    opt_ascii = ('-a', '') in options
    opt_shallow = ('-x', '') in options
    opt_listfilenames = ('-l', '') in options # pour un repertoire avec des listes de nom de fichier, ou directement une liste de nom

    if opt_listfilenames:
        list_path = args[0]
        text_dir = args[1]
        gold_dir = args[2]
        output_dir = args[3] if len(args) > 3 else None
    else: 
        text_dir = args[0]
        gold_dir = args[1]
        output_dir = args[2] if len(args) > 2 else None


    if output_dir:
        try:
            my_text_dir = os.path.dirname(text_dir)
            output_dir = os.path.join(output_dir, my_text_dir)
            print output_dir
            os.makedirs(output_dir)
        except OSError:
            pass
        
        
        if opt_shallow:
            # pour utiliser l'option -> rajouter dans le getopt
            last_dir = "shallow/"
            
        else:
            last_dir = "deep/"
        if opt_listfilenames:
            pass 
    else:
        last_dir = ""

    # --------------------- INIT FILES choix type ---------------------
    # 2 parcourir un seul fichier avec une liste de fichiers 
    if opt_listfilenames:
        f = open(list_path, 'r')
        files = []
        for fn in f.readlines():
            files.append(fn.rstrip('\n'))
        f.close()
    else:
        # 1 parcourir les fichiers d'un repertoire
        files = os.listdir(text_dir)  # filenames should be the same in text_dir/ and gold_dir/
    
    n_files = len(files)

    # -----------------------------------------------------------------
    if n_files==0:
        print "text", text_dir

    sum = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    ss = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

    if not opt_noheader:
        # print "file\tF\tP\tR\tF.tag\tP.tag\tR.tag\tTP\tFP\tFN\tTP.tag\tFP.tag\tFN.tag"
        header="file\tF\tP\tR\tF.tag\tP.tag\tR.tag\tTP\tFP\tFN\tTP.tag\tFP.tag\tFN.tag\tF.quadri\tP.quadri\tR.quadri\tTP.quadri\tFP.quadri\tFN.quadri\n"
        if not output_dir:
            print header
        else:
            res_file_name = last_dir[:-1]+"_res.csv"
            fres = open(os.path.join(output_dir, res_file_name), 'w')

    
    # ______________________________________________________________________
    # ------------------------------- exec ---------------------------------
    # ----------------------------------------------------------------------
    n_processed = 0 
    filenotfound = []
    for filename in files:
        text_file = os.path.join(text_dir, filename)
        gold_file = os.path.join(gold_dir, filename)
        
        if not os.access(gold_file, os.R_OK):
            filenotfound.append(gold_file)
            continue  # original and cleaned files don't always match in CleanEval gold standard
            
        # ----------------------------------------------------------------------
        # ----------------------------- normalisation --------------------------
        # ----------------------------------------------------------------------
        file_content = slurp_file(text_file)
        gold_content = slurp_file(gold_file)
        if opt_shallow:
            # normalize shallow
            text = normalize(file_content, filename, last_dir, opt_ascii, opt_unlabelled, True)
        else: 
            # nomalize deep
            text = normalize(file_content, filename, last_dir, opt_ascii, opt_unlabelled, True)

        gold = normalize(gold_content, filename, last_dir, opt_ascii, opt_unlabelled, True)

        text_words = re_WS.split(text)
        gold_words = re_WS.split(gold)

        # ----------------------------------------------------------------------
        # ---------------------- alignement + eval -----------------------------
        # ----------------------------------------------------------------------
        alignment = SequenceMatcher(None, text_words, gold_words)
        diff = make_diff(alignment, text_words, gold_words)
        eval_list = evaluate(diff)
        # Ajout : la meme chose en quadrigrammes
        text_quadris = quadri(text)
        gold_quadris = quadri(gold)
        alignment_quadris = SequenceMatcher(None, text_quadris, gold_quadris)
        diff_quadris = make_diff(alignment_quadris, text_quadris, gold_quadris)
        eval_list_quadris = evaluate(diff_quadris)
        F, P, R,_,_,_,TP,FP,FN,_,_,_ = eval_list_quadris # on extrait les stats sur les quadrigrammes
        l_stats = [F,P,R,TP,FP,FN]
        eval_list = list(eval_list) # Pour pouvoir ajouter, il faut une liste
        for stat in l_stats:
            eval_list.append(stat)
        eval_list = tuple(eval_list) # On re-caste pour la suite

        file_result = filename[:40] + "\t" + ("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%d\t%d\t%d\t%d\t%d\t%d\t%.2f\t%.2f\t%.2f\t%d\t%d\t%d\n" % eval_list)
        if output_dir:
            fres.write(file_result)            
        else:
            print file_result

        for i, x in enumerate(eval_list):
            sum[i] += x
            ss[i] += x ** 2

        if output_dir:
            align_file = os.path.join(output_dir, os.path.join(last_dir, os.path.splitext(filename)[0]+".align"))
            write_alignment(diff, align_file)

        n_processed += 1

    if n_processed < n_files:
        print >> sys.stderr, "Warning: %d files not found in gold standard (skipped)" % (n_files - n_processed)
        print >> sys.stderr, filenotfound

    ## calculate micro-averaged word-level accuracy / tag accuracy
    _,_,_,_,_,_,tp,fp,fn,tag_tp,tag_fp,tag_fn,_,_,_,quadri_tp,quadri_fp,quadri_fn = sum
    triplets_data = [[tp,fp,fn],[tag_tp,tag_fp,tag_fn],[quadri_tp,quadri_fp,quadri_fn]] # vont servir au calcul
    liste_stats = [] # va stocker les resultats
    for TP,FP,FN in triplets_data:
        l = [] # pour stocker precision,rappel et f-score pour chaque donnee analysee
        n_text = TP + FP if TP + FP > 0 else 1
        n_gold = TP + FN if TP + FN > 0 else 1
        precision = float(TP) / n_text
        l.append(precision)
        recall = float(TP) / n_gold
        l.append(recall)
        precision_plus_recall = precision + recall if precision + recall > 0 else 1
        f_score = 2 * precision * recall / precision_plus_recall
        l.append(f_score)
        liste_stats.append(l)
    precision,recall,f_score = liste_stats[0]
    tag_precision,tag_recall,tag_f_score = liste_stats[1]
    quadri_p,quadri_r,quadri_f = liste_stats[2]

    # -------------------------------------------------------------------
    # ----------------- mise en forme des resultats ---------------------
    # -------------------------------------------------------------------
    eval_list = (100 * f_score, 100 * precision, 100 * recall, 100 * tag_f_score, 100 * tag_precision, 100 * tag_recall, tp, fp, fn, tag_tp, tag_fp, tag_fn,quadri_f*100,quadri_p*100,quadri_r*100,quadri_tp,quadri_fp,quadri_fn, n_processed, len(os.listdir(gold_dir))) 
    sum_message="_______\t_______\tsum\t_______\t_______\n"
    sum_message+="file\tF\tP\tR\tF.tag\tP.tag\tR.tag\tTP\tFP\tFN\tTP.tag\tFP.tag\tFN.tag\tF.quadri\tP.quadri\tR.quadri\tTP.quadri\tFP.quadri\tFN.quadri\tnb.files\tnb.gold\n"
    sum_message+="total" + "\t" + ("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%d\t%d\t%d\t%d\t%d\t%d\t%.2f\t%.2f\t%.2f\t%d\t%d\t%d\t%d\t%d\n" % eval_list)
    sum_message+="_______\t_______\t_______\t_______\t_______\n"
    
    try:
        mean = [ x / n_files for x in sum ]
    except ZeroDivisionError:
        print "erreur"
        print "x", x
        print "n_f", n_files
    
    if n_files-1 == 0:
        #mean = [ 0 for x in sum ]
        sd = [ 0 for x, y in zip(ss, mean) ]
    else:
        
        sd = [ sqrt((x - n_files * y ** 2) / (n_files - 1)) for x, y in zip(ss, mean) ]
    if opt_summary:
        stat_message="_______\t_______\t*stat*\t_______\t_______\n"
                     
        is_labelled = "unlabelled" if opt_unlabelled else "labelled"
        if n_processed < n_files:
            stat_message+="Evaluation report for on %d out of %d files:\n" % (n_processed, n_files)
	else:
            stat_message+="Evaluation report for on %d files:\n" % (n_files)	
        
        stat_message+="Word-level accuracy (macro-averaged):\n"
        stat_message+="  F-score:   %6.2f%% %s %.2f%%\n" % (mean[0], '+/-', sd[0])
        stat_message+="  Precision: %6.2f%% %s %.2f%%\n" % (mean[1], '+/-', sd[1])
        stat_message+="  Recall:    %6.2f%% %s %.2f%%\n" % (mean[2], '+/-', sd[2])
        stat_message+="Segmentation accuracy (%s):\n" % (is_labelled)
        stat_message+="  F-score:   %6.2f%% %s %.2f%%\n" % (mean[3], '+/-', sd[3])
        stat_message+="  Precision: %6.2f%% %s %.2f%%\n" % (mean[4], '+/-', sd[4])
        stat_message+="  Recall:    %6.2f%% %s %.2f%%\n" % (mean[5], '+/-', sd[5])
        stat_message+="Quadrigramm-level accuracy (macro-averaged):\n"
        stat_message+="  F-score:   %6.2f%% %s %.2f%%\n" % (mean[12], '+/-', sd[14])
        stat_message+="  Precision: %6.2f%% %s %.2f%%\n" % (mean[13], '+/-', sd[13])
        stat_message+="  Recall:    %6.2f%% %s %.2f%%\n" % (mean[14], '+/-', sd[14])
        stat_message+="Word-level accuracy (micro-averaged):\n"
        stat_message+="  F-score:   %6.2f%%\n" % (100 * f_score)
        stat_message+="  Precision: %6.2f%%\n" % (100 * precision)
        stat_message+="  Recall:    %6.2f%%\n" % (100 * recall)
        stat_message+="Segmentation accuracy (%s, micro-averaged):\n" % (is_labelled)
        stat_message+="  F-score:   %6.2f%%\n" % (100 * tag_f_score)
        stat_message+="  Precision: %6.2f%%\n" % (100 * tag_precision)
        stat_message+="  Recall:    %6.2f%%\n" % (100 * tag_recall)
        stat_message+="Quadrigramm-level accuracy (micro-averaged):\n"
        stat_message+="  F-score:   %6.2f%%\n" % (100 * quadri_f)
        stat_message+="  Precision: %6.2f%%\n" % (100 * quadri_p)
        stat_message+="  Recall:    %6.2f%%\n" % (100 * quadri_r)
        stat_message+="_______\t_______\t_______\t_______\t_______\n"
	# modification berenguer : 2 lignes suivantes misent en commentaire car erreur a l'execution
    #else:
        #nb_files

    resume_message ="_______\t_______\tResume\t_______\t_______\n"
    resume_message+="[INFO] COMPARE "+args[0]+" TO "+args[1]+"\n"
    # recuperation des arguments d'appel utilisateur
    args = ""
    for i in sys.argv:
        args += i+'\t'
    resume_message+="[INFO OPT]\tcall:\n%s\n" % (args)
    if opt_total:
        resume_message+="[INFO OPT]\tresults:\ttotal precision/recall for all files\n"
    if opt_summary:
        resume_message+="[INFO OPT]\tresults:\tcalculate summary statistics\n"

    if opt_unlabelled:
        resume_message+="[INFO OPT]\tevaluate:\tunlabelled\n"
    else:
        resume_message+="[INFO OPT]\tevaluate:\tlabelled\n"

    if opt_shallow:
        resume_message+="[INFO OPT]\tpre-processing:\tshallow\n"
    else:
        resume_message+="[INFO OPT]\tpre-processing:\tdeep\n"

    if opt_ascii:
        resume_message+="[INFO OPT]\tremove non-ASCII characters before comparison\n"
    else:
        resume_message+="[INFO OPT]\tcould still have non-ASCII characters\n"
    if opt_listfilenames:
        resume_message+="[INFO OPT]\trunning from filenames list\n"
    else:
        resume_message+="[INFO OPT]\trunning from scraped files directory directly\n"
 
    resume_message+="[INFO FILES]\tEvaluation report for on %d out of %d files\n" % (n_processed, n_files)
    # ajouter nb char moyen par doc du gold uniquement des body 1 avec balises 2 sans balises
    t2 = time()
    resume_message+="[INFO]\texec time:\t"+str(t2-t1)+"sec\n"

    now = datetime.datetime.now()
    resume_message+="[INFO]\tCurrent date and time:\t"
    resume_message+=str(now)+"\n"
    resume_message+="_______\t_______\t_______\t_______\t_______\n"
    
    if output_dir:
        if opt_summary:
            # ecrire dans le fichier de stats
            stat_file_name = last_dir[:-1]+"_res_stat.csv"
            fstat = open(os.path.join(output_dir, stat_file_name), 'w')
            fstat.write(stat_message)
            fstat.close()

        # ecrire dans le fichier de resultat
        fres.write(sum_message)
        fres.write(resume_message)
        fres.close()
            
    else:  
        print sum_message
        if opt_summary:
            print stat_message
        print resume_message


