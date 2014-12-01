Importer le projet sous Eclipse -> Run
	MiniLib:contiens les fichiers nécessaire pour créer le plus gros Bucket trouvé
	MiniLib2:Contiens les fichier nécessaire pour crée un bucket de taille 11
	MiniLib2:Contiens un fichier unique pour crée un bucket de 1 élement	
	Pour obtenir l'ensemble des callStack allez sur : http://sachaproject.gforge.inria.fr/bug-reports-trace-anywhere.zip

	Exemple de main:
	-Pour obtenir la similarité entre deux CallStack
	
		Parser p = new Parser();
        	CallStack c1 = p.read("Emplacement/Json");
        	CallStack c2 = p.read("Emplacement/Json");
        	Processor processor = new Processor();
        	processor.similarity(c1, c2);

	
	-Pour calculer la similarité entre deux Bucket

		Parser p = new Parser();
		Processor processor = new Processor();
		//récupération des callStacks
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

	-Pour créer les buckets issue des crash (en utilisant leur ID) , dans le but donc d'avoir les buckets originaux
		-Mettre les fichier dans un repertoire à la racine du projet
		
		String directory="Votre repertoire";
		String resultDirectory="Le repertoire contenant vaut résultat";
		Parser p = new Parser();
		ArrayList<CallStack> allCallStack = p.readAll(directory); // add all id
		for (int i = 0; i < allCallStack.size(); i++) {

			p.addGroupIdAndDuplicateId(allCallStack.get(i), directory + "/"
					+ allCallStack.get(i).getFilename());
		}
		Bucket bucket = new Bucket();
		ArrayList<Bucket> allBuck = bucket.creationAllBucket(allCallStack);
		File rep = new File(resultDirectory);//création du répértoire
		rep.mkdir();
		for (int i = 0; i < allBuck.size(); i++) {
			try {
				allBuck.get(i).createFile(resultDirectory);
			} catch (IOException e) { //
			}
		}
	-Pour créer les buckets à l'aide de notre algorithme
		Bucket buck=new Bucket();
		Processor processor = new Processor();
		String directory = "emplacement/des/FichierJson";
		String directoryResultat ="emplacement/des/résultats";
		int minSimilarity = 1;
		Parser p = new Parser();
		ArrayList<Bucket> buckets =p.oneFileOneBucket(directory);
		buckets=processor.bucketize(buckets, minSimilarity);
		File rep = new File(directoryResultat);//création du répértoire
		rep.mkdir();
		for (int i = 0; i < buckets.size(); i++) {
			try {
				buckets.get(i).createFile(directoryResultat);
			} catch (IOException e) {
			}
		}
	