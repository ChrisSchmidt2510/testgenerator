![build](https://github.com/ChrisSchmidt2510/testgenerator/workflows/build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.nvg%3Atestgenerator-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.nvg%3Atestgenerator-parent)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.nvg%3Atestgenerator-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=de.nvg%3Atestgenerator-parent)
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
