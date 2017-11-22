JC = javac
RM = rm -rf
.SUFFIXES: .java .class
.java.class:
				$(JC) $*.java

CLASSES = \
				Server.java \
				Client.java \
				ServerExtra.java \
				ClientExtra.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
				$(RM) *.class