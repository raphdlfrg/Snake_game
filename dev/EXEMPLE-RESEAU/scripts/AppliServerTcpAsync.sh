
#!/bin/bash

# le nom (complet) de classe d'application à lancer pour ce script.
# CETTE LIGNE EST LA SEULE LIGNE À CHANGER POUR ADAPTER CE SCRIPT À UNE AUTRE CLASSE.
CLASS=tsp.pro3600.appli.AppliServerTcpAsync

# à partir d'ici le script est générique pour toutes les classes d'application du projet.

# tous les arguments du script du programme sont sauvegardés dans la variable ARGS
ARGS=$*

# ATTENTION ce script doit être lancé depuis la racine du projet (là où se trouve le fichier pom.xml)
PROJECTROOT=$(pwd)

# le caractère de séparation des bibliothèques dans la variable CLASSPATH. Depuis 2019 le caractère ":" est portable sur
# tous les systèmes, y compris Windows.
PATHSEP=':'

# On ajoute aux éventuelles bibliothèques déjà présente dans la variable CLASSPATH
#    -> toutes les bibliothèques présentes dans {PROJECTROOT}/target/dependency
#    -> le dossier où maven installe les classes du projet
#CLASSPATH=${CLASSPATH}${PATHSEP}${PROJECTROOT}/target/dependency/*${PATHSEP}${PROJECTROOT}/target/classes

# la version en commentaire ci dessus fonctionne sur linux et Mas OS, mais pause deux problèmes sous windows:
# 1> les * font partie des caractères interdits dans les noms de fichier, et il n'est pas possibles des les utiliser le CLASSPATH
# 2> le CLASSPATH ne peut pas commencer ou finir par un ":". Il faut donc vérifier que la variable CLASSPATH existe avant de la placer en début du nouveau CLASSPATH.
# Voici donc une version plus compliquée (test et boucle) qui est portable sur windows

# ajout du répertoire de classes du projet
if [ -z "${CLASSPATH}" ] ; then
  export CLASSPATH=${PROJECTROOT}/target/classes
else
  export CLASSPATH=${CLASSPATH}${PATHSEP}${PROJECTROOT}/target/classes
fi

# ajout de tous les jar des bibliothèques dont dépend le projet par une boucle for
for dep in ${PROJECTROOT}/target/dependency/*.jar ;  do
  export CLASSPATH=${CLASSPATH}${PATHSEP}${dep}

done


# Il ne reste plus qu'à lancer la commande avec toutes les variables construites
CMD="java -cp ${CLASSPATH} ${CLASS} ${ARGS}"

$CMD

# on quitte avec le statut d'exit de la dernière commande: la commande java
exit $?
