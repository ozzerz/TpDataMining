M2 IAGL Lille 1 - IDL

M�thode de Classification de Rapports de Crash bas�e sur la similarit� des Call Stack
====================

Authors
---------------------
Maxime Chaste  
Pierre-Philippe Berenguer

Ex�cution
---------------------
Importer le projet sous Eclipse -> Run
Pour les choix de bucketing, similarite etc, utiliser les exemples ci dessous pour faire les appels dans le Main.

[github du projet](https://github.com/ozzerz/TpDataMining.git)

M�thode
---------------------
Regroupement de piles de crash par bucket (cluster) � l'aide d'une mesure de similarit� Position Model Dependent (PDM) :
[ReBucket article Windows](http://research.microsoft.com/en-us/groups/sa/rebucket-icse2012.pdf)

Mini-corpus : callstacks Eclispe
--------------------
	mini-lib4 : plusieurs fichiers pour faire des tests sans utiliser tous les rapports d'erreur
	mini-lib3 : contient les fichiers n�cessaires pour cr�er le plus gros Bucket trouv�
	mini-lib2 : contient les fichiers n�cessaires pour cr�er un bucket de taille 11
	mini-lib : contient un fichier unique pour cr�er un bucket de 1 �lement	
	Pour obtenir l'ensemble des callStacks allez sur : http://sachaproject.gforge.inria.fr/bug-reports-trace-anywhere.zip

Utilisation
-------------------
	Exemple de main:
	-Pour obtenir la similarit� entre deux CallStack
	
		Parser p = new Parser();
        	CallStack c1 = p.read("Emplacement/Json");
        	CallStack c2 = p.read("Emplacement/Json");
        	Processor processor = new Processor();
        	processor.similarity(c1, c2);

	
	-Pour calculer la similarit� entre deux Bucket

		Parser p = new Parser();
		Processor processor = new Processor();
		//r�cup�ration des callStacks
        	CallStack c1 = p.read("Emplacement/Json");
        	CallStack c2 = p.read("Emplacement/Json");
		CallStack c3 = p.read("Emplacement/Json");
        	CallStack c4 = p.read("Emplacement/Json");
		//creation des buckets
		Bucket bucket1 = new Bucket("NomBucket");
		Bucket bucket2 = new Bucket("NomBucket2");
		Bucket1.addCallStack(c1);
		Bucket1.addCallStack(c2);
		Bucket2.addCallStack(c3);
		Bucket2.addCallStack(c4);
		processor.similarity(bucket1,bucket2)

	-Pour cr�er les buckets issue des crash (en utilisant leur ID) , dans le but donc d'avoir les buckets originaux
		-Mettre les fichier dans un repertoire � la racine du projet
		
		String directory="Votre repertoire";
		String resultDirectory="Le repertoire contenant vaut r�sultat";
		Parser p = new Parser();
		ArrayList<CallStack> allCallStack = p.readAll(directory); // add all id
		for (int i = 0; i < allCallStack.size(); i++) {

			p.addGroupIdAndDuplicateId(allCallStack.get(i), directory + "/"
					+ allCallStack.get(i).getFilename());
		}
		Bucket bucket = new Bucket();
		ArrayList<Bucket> allBuck = bucket.creationAllBucket(allCallStack);
		File rep = new File(resultDirectory);//cr�ation du r�p�rtoire
		rep.mkdir();
		for (int i = 0; i < allBuck.size(); i++) {
			try {
				allBuck.get(i).createFile(resultDirectory);
			} catch (IOException e) { //
			}
		}

	-Pour cr�er les buckets � l'aide de notre algorithme
		
		Bucket buck=new Bucket();
		Processor processor = new Processor();
		String directory = "votrelib/avecvos/Json";
		String directoryResultat ="emplacement/retour";
		double minSimilarity = 0.6 ;
		Parser p = new Parser();
		ArrayList<Bucket> buckets =p.oneFileOneBucket(directory);
		buckets=processor.bucketize(buckets, minSimilarity);
		File rep = new File(directoryResultat);//cr�ation du r�p�rtoire
		rep.mkdir();
		for (int i = 0; i < buckets.size(); i++) {
			try {
				buckets.get(i).createFile(directoryResultat,buckets.get(i).getCallStacks().get(0).getFilename());
			} catch (IOException e) {
			}
		}


	
