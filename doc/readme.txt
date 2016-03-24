Strategic Game Server 1.0
--------------------------

Bestandsnaam:                          Omschrijving:
gamemodules                            Map met GameModule JAR-bestanden.
gamemodule-1.0.jar                     GameModule interface.
functiondiagram.vsd                    Diagram met beschikbaarheid van functies
                                       tijdens de GameModule statussen.
protocol.txt                           Protocol-documentatie.
readme.txt                             Dit informatie bestand.
sequencediagram.vsd                    Sequentiediagram van het gebruik van de
                                       GameModule.
settings.conf                          Configuratie-bestand van de game server.
statediagram.vsd                       Toestandsdiagram van de GameModule.
StrategicGameServer-1.0.bat            Start de game server.
StrategicGameServer-1.0.debug.bat      Start de game server in debug modus.
StrategicGameServer-1.0.exe            De game server, uitvoerbaar bestand.
StrategicGameServer-1.0.jar            De game server, uitvoerbaar JAR-bestand.
StrategicGameServer-1.0.src.jar        De game server, Java-broncode.


settings.conf:
--------------------------
Het configuratie-bestand bevat instellingen voor de game server.
Als dit bestand niet bestaat, dan wordt deze aangemaakt met de
standaardwaarden.

De waarden die het configuratie-bestand bevat:

Setting-naam:              Standaardwaarde:  Omschrijving:
turn_timelimit_sec         10                Tijdslimiet van een beurt (sec.)
tournament_turn_delay_sec  0                 Vertraging tussen zetten tijdens
                                             een tournament, in seconden.
tournament_disconnect      remove            Actie die ondernomen moet worden
                                             als een speler de verbinding
                                             verbreekt tijdens een tournament.
                                             Mogelijke waarden: loss, remove.
gamemodule_path            gamemodules       Map met GameModule JAR-bestanden.
                                             Deze wordt automatisch aangemaakt.
listener_port              7789              De TCP-poort waarop de game server
                                             luistert naar verbindingen.

gamemodule-1.0.jar:
--------------------------
Dit JAR-bestand bevat de interface IGameModule en de abstracte klasse
AbstractGameModule. Een game implementatie dient de AbstractGameModule uit te
breiden. Dit JAR-bestand bevat zowel de Java-bestanden als de Class-bestanden.

Map met GameModule JAR-bestanden:
--------------------------
Deze map bevat de GameModule JAR-bestanden. De map wordt bij het starten van
de game server doorzocht op JAR-bestanden. Deze JAR-bestanden worden doorzocht
op klassen die de klasse AbstractGameModule uit gamemodule-1.0.jar uitbreiden.
De meegeleverde GameModule JAR-bestanden bevatten Java-broncode.
