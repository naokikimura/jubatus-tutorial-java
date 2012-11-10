jubatus-tutorial-java
=====================

see http://jubat.us/en/tutorial.html

Quick Start
--------------------

    git clone git://github.com/naokikimura/jubatus-tutorial-java.git
    cd jubatus-tutorial-java

    curl -O http://people.csail.mit.edu/jrennie/20Newsgroups/20news-bydate.tar.gz
    tar -xvzf 20news-bydate.tar.gz -C src/main/resources/example/

    jubaclassifier --rpc-port=9190 --name=tutorial &

    mvn -q compile exec:java -Dexec.args="-p 9190"
