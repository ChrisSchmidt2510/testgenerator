# testgenerator
A Java Agent that generates at application runtime a Test with all needed Objects for the marked Method.

The Projects testgenerator-full and testgenerator-rt are only build-projects.
The full Project will be used in standard Java-applications
The rt Project will be used in Enterprise Java-applications

In dem Projekt testgenerator-agent befindet sich der eigentliche Java-agent der der JVM bekannt gemacht werden muss.
Hier finden auch die Modifizierungen an zu ladenen Klassen durch. Zum einen wird die in der Konfiguration angegebene Methode so modifziert,
das sobald die Methode aufgerufen wird, werden die Werte Methodenparameter zwischengespeichert. 
(In einer für die Anwendung später darstellbaren Weise) 
Desweiteren wird vor dem Rückgabeparameter noch der Testfall in das angegebene Verzeichnis generiert.
Zum anderen werden die Klassen in dem in der Konfiguration angegebenen Package verändert, das um die Felder ein Proxy gepackt wird. 
Dieser Proxy ist notwendig um in dem generierten Testfall nur verwendete Felder zu generieren. 
Außerdem wird in jeder BL-KLasse noch eine statische Variable angelegt, die die das Klassenmodel bereithält. 
Dies ist ebenfalls für die Generierung des Testfalls notwendig.

Das Projekt testgenerator-classdatamodel-rt stellt das Klassenmodel für die Laufzeit der Application zu Verfügung

Das Projekt testgenerator-core liegen Klassen die über alle Module hinweg erreichbar sein sollen. 
Außerdem soll hier in Zukunft die Konfiguration des Testgenerators per XML geladen werden können.

Das Projekt testgenerator-generation kümmert sich um die Generierung des Testfalls. Ist bis jetzt noch überhaupt nicht implementiert.

Das Projekt testgenerator-proxy enthält die Proxys die um die Felder der BL-Klassen gewrapped werden.

Das Projekt value-tracker wandelt die Methodenparameter, in eine später verarbeitetbare Darstellweise um.

Die Projekte classdatamodel-rt, proxy, core, value-tracker und generation müssen nur zur eigentlichen Laufzeit der Applikation zur Verfügung stehen.

Next Steps:
Aufsplitten in AgentProperties und RuntimeProperties(JVM Arguments at Runtime:https://stackoverflow.com/questions/1490869/how-to-get-vm-arguments-from-inside-of-java-application)

NamingService für ObjectValueTracker

Logging:
Erstellung Logger-Instanz wie bei log4j
Laden Logger-conf evtl per XML

Testclassgeneration
Interface für die Generierung der Testklasse definieren
Default-Implementierung des Interfaces schreiben
Andere Implementierungen können in der Config angegeben werden

Bugfixes:
ObjectValueTracker mit Simple-bl Projekt
Analysis-Package mit Sample-bl Projekt

Implementierung weiterer Analysis Methoden(Map u. Arrays)n

