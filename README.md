# [Semesteroppgave 1: “Rogue One oh one”](https://retting.ii.uib.no/inf101.v18.sem1/blob/master/SEM-1.md)


* **README**
* [Oversikt](SEM-1.md) – [Praktisk informasjon 5%](SEM-1.md#praktisk-informasjon)
* [Del A: Bakgrunn, modellering og utforskning 15%](SEM-1_DEL-A.md)
* [Del B: Fullfør basisimplementasjonen 40%](SEM-1_DEL-B.md)
* [Del C: Videreutvikling 40%](SEM-1_DEL-C.md)

Dette prosjektet inneholder [Semesteroppgave 1](SEM-1.md). Du kan også [lese oppgaven online](https://retting.ii.uib.no/inf101.v18.oppgaver/inf101.v18.sem1/blob/master/SEM-1.md) (kan evt. ha små oppdateringer i oppgaveteksten som ikke er med i din private kopi).

**Innleveringsfrist:**
* Del A + minst to deloppgaver av Del B skal være ferdig til **fredag 9. mars kl. 2359**. 
* Hele oppgaven skal være ferdig til **onsdag 14. mars kl. 2359**

(Kryss av under her, i README.md, så kan vi følge med på om du anser deg som ferdig med ting eller ikke. Hvis du er helt ferdig til den første fristen, eller før den andre fristen, kan du si fra til gruppeleder slik at de kan begynne å rette.)

**Utsettelse:** Hvis du trenger forlenget frist er det mulig å be om det (spør gruppeleder – evt. foreleser/assistenter hvis det er en spesiell situasjon). Hvis du ber om utsettelse bør du helst være i gang (ha gjort litt ting, og pushet) innen den første fristen.
   * Noen dagers utsettelse går helt fint uten begrunnelse, siden oppgaven er litt forsinket.
   * Hvis du jobber med labbene fremdeles, si ifra om det, og så kan du få litt ekstra tid til å gjøre ferdig labbene før du går i gang med semesteroppgaven. Det er veldig greit om du er ferdig med Lab 4 først.
   * Om det er spesielle grunner til at du vil trenge lengre tid, så er det bare å ta kontakt, så kan vi avtale noe. Ta også kontakt om du [trenger annen tilrettelegging](http://www.uib.no/student/49241/trenger-du-tilrettelegging-av-ditt-studiel%C3%B8p). 
   

# Fyll inn egne svar/beskrivelse/kommentarer til prosjektet under
* Levert av:   *Oda Inanna Stene* (*kuv009*)
* Del A: [x] helt ferdig, [ ] delvis ferdig
* Del B: [x] helt ferdig, [] delvis ferdig
* Del C: [x] helt ferdig, [ ] delvis ferdig
* [x]hele semesteroppgaven er ferdig og klar til retting!

# Del A
## Svar på spørsmål

**a)**

Ettersom IItem er et grensesnitt vet vi at alle metoder i IItem kan kalles på alle objekter av klasser som enten implementerer IItem eller implementerer andre grensesnitt som utvider IItem. Her er noen ting vi kan anta om tilstanden til ting etter å ha lest grensesnittet IItem:
Tilstanden til ting inkluderer:
* artikkel, et navn, og et symbol
* maksimale helsepoeng og nåværende helsepoeng
* forsvars-poengsum
* størrelse

IActor utvider IItem, altså er alle aktører ting. Tilstanden til aktører må inkludere alt som tilstanden til ting har, og i tillegg:
* angrepspoengsum

IPlayer og INonPlayer utvider IActor. Ettersom spillere blir styrt av brukeren og ikke-spillere blir styrt av maskinen har de ulik oppførsel, men av grensesnittene ser det ikke ut som at tilstanden trenger å være mer komplisert enn tilstanden til alle aktører

IMapView har metoder for å håndtere kart, så objekter av klasser som implementerer IMapView burde ha eller være kart. Hvis kartet har eller er en generisk liste av generiske lister så kan den ha all informasjonen som er nødvendig for å ha den oppførselen som grensesnittet legger opp til. 

~~Objekter som implementerer IGameMap forholder seg til grafikk-biblioteket, i motsetning til objekter som implementerer IMapView som ikke gjør det. Vet ikke hva dette grensesnittet er til enda.~~ IGameMap utvider IMapView. Dokumentasjonen forteller at den eneste klassen som skal bruke IGameMap er Game-klassen. Det betyr at Game-klassen får mulighet til å gjøre ting med kart som andre klasser ikke får lov til, fordi de andre klassene bare kan bruke metodene som står i IMapView. Oppførselen er annerledes, men jeg tror ikke tilstanden behøver å være det.

**b)** 

IPlayer og INonPlayer utvider IActor, som igjen utvider IItem.
IGameMap utvider IMapView. 

Alle de nevnte grensesnittene tar imot IGame. Ingen ser ut til å ha game som innkapslet tilstand fordi den er input til flere av metodene.

IItem tar imot IGame, IEvent, ITurtle og IItem. 
IPlayer tar også imot en KeyCode (input fra brukeren), og det gjør ikke INonPlayer. 
IMapView tar imot og returnerer IItem og IActor, men skiller ikke mellom IPlayer og INonPlayer.
IGameMap tar imot ITurtle, og det gjør ikke IMapView. 

Det er et mønster i at relasjonene mellom grensesnittene går én vei. For eksempel forholder kart seg til ting (ettersom kart tar inn og returnerer ting), men tingene ser ikke ut til å forholde seg til at de befinner seg på et kart.

**c)** 

~~Objekter av klasser som implementerer IMapView må ikke ha tegnemetode, mens objekter av klasser som implementerer IGameMap må ha det. En mulig grunn kan være at objekter som implementerer IMapView skal fungere som grafikkfrie mock-objekter som ellers brukes på samme måte som IGameMap, men bare brukes til testing.~~ 

Dette fant jeg ut senere: GameMap implementerer både IMapView og IGameMap. Det står i IGameMap-grensesnittet at det inneholder metoder som bare er for game-klassen. Når Rabbit eller Player henter kartet ved hjelp av game.getMap(), så får de et objekt av typen IMapView. Så lenge det bare er Game-klassen som ser på kartet som et IGameMap-objekt, så er det bare den som kan rydde, fjerne døde ting, og kalle draw()-metoden. Jeg tror grunnen til at det er to grensesnitt for kart er at dere ønsket å sørge for at disse metodene bare kunne kalles fra Game, noe som gjør programmet mer oversiktlig og lettere å feilsøke.  

**d)** 

Aktører styrt av brukeren skal vente på input og aktører styrt av maskinen skal ikke. Ettersom klasser for spillere og klasser for maskinstyrte aktører skal brukes på ulik måte er det naturlig at de har ulike grensesnitt slik at man ikke kommer i skade for å bruke dem feil. Ulike grensesnitt gjør det også lett å finne ut hvilke aktører som er spillere og hvilke som er maskinstyrte fordi man kan bruke "instance of".

**e)**

Jeg gjettet at tilstanden på konstante verdier (som fks. max health points) skulle være lagret sånn: 
<pre><code>private final int MAX_HEALTH = 10; 

@Override
public int getMaxHealth(){
  return MAX_HEALTH;
}</code></pre>

Men de var lagret sånn:
<pre><code>@Override
public int getMaxHealth() {
	return 10;
}</code></pre>
I begge tilfeller er tilstanden innkapslet og oppførselen den samme.

**f)** 

Kaniner i spillet vet ikke de hvor de er, men alle metodene som krever en posisjon har et spill-objekt som input.

Spillet vet hvem som er currentActor og hva som er currentLocation og bruker den informasjonen til å svare når kaninen spør hvor den er, om den kan gå i en bestemt retning, eller hva som finnes på et sted.

Spillet henter denne informasjonen fra kartet, som har plasseringen til alle ting lagret i en IdentityHashMap.

**g)** 

Game-objektet endrer currentactor og currentLocation før den kaller doTurn-metoden på aktøren. 

doTurn-metoden til en gitt kanin kjører altså bare når den kaninen er satt til currentActor og dens posisjon er satt til currentLocation.

currentLocation blir oppdatert ved at Game-objektet kaller getLocation på GameMap-objektet, som slår opp i en IdentityHashMap (items) og får ut posisjonen. 


**Smarte kaniner**

**a)** Kaninene lever ikke overraskende lenger når de har flere helsepoeng ved start.

**b)** Når alle kaninene prøver å gå nordover hver gang blir de stående mye stille på grunn av IllegalMoveException. Til slutt blir de stående i klynger inntil veggen.

**c)** Når alle kaninene går i første mulige retning går de nesten alltid østover. Gulrøttene på vestsiden av kartet blir sjelden spist, og kaninene som starter vest for andre kaniner får nesten ingen gulrøtter.

**d)** Jeg lagde en metode som heter chooseCarrotIfPossible, og som gjør at kaninene sjekker blant de mulige retningene om noen av dem har gulrøtter og i så fall går dit. Nå er de litt flinkere til å finne gulrøtter, men det er fortsatt rom for forbedringer.

**e)** Implementerte getPossibleMoves() i Game.

**Smarte gulrøtter**

**a)** Jeg vil ikke egentlig si at det gikk dårligere med kaninene da jeg satte gulrøttenes hp til 0 istedenfor -1. Noen kaniner gikk det veldig bra med. Det som skjer er at kaninene som finner en gulrot blir stående stille og spise gulroten for alltid, selv om gulroten har hp lik 0. Grunnen er at kaninens doTurn-metode returnerer tidlig (før den har beveget seg) så lenge kaninen har funnet en gulrot. Eaten blir satt til verdien som kaninen "angrep" gulroten med, og avhenger ikke av helsepoengene som gulroten har igjen.  Ettersom gulroten aldri blir fjernet fra kartet blir kaninene som har funnet en gulrot stående og spise gulrøtter med hp = 0 for alltid, mens alle de adre kaninene dør.

**b)** Jeg satte et break-punkt på doTurn-metoden og fant ut at den aldri ble kjørt. Så bestemte jeg meg for å lage en ny interface (IActiveItem), for ting som har en doTurn-metode. Jeg endret beginTurn() i Game slik at den la alle ting som implementerer IActiveItem til en liste som heter activeItems, og la til en while-løkke i doTurn() i Game som kaller doTurn-metoden til alle aktive ting en gang per runde.

(Det gikk ikke på første forsøk, og jeg fikk en merkelig feil som gjorde at døde kaniner kunne bli levende igjen. Jeg snakket en annen student og hun foreslo å la Carrot implementere INonPlayer. Da ble doTurn-metoden kalt hver runde, men kaninene kunne ikke lenger spise gulrøttene. Til slutt gikk jeg tilbake til løsningen med IActiveItem og tipset noen andre på datalabben om at det kunne gjøres sånn.)

**c)** ~~I Game lagde jeg en metode som legger til nye gulrøtter med en viss prosent sannsynlighet. Den kalles fra Main sin doTurn()-metode.~~ Denne slettet jeg senere fordi jeg ikke hadde bruk for den.


# Del B
## Svar på spørsmål

a) Jeg har håndtert naboene på samme måte som tidligere. Jeg vurderte å lage en rekursiv funksjon der
man henter inn naboer av naboer osv. men kom fram til at det var minst like praktisk å gjøre det slik jeg
har gjort. Fordelen med mange av de andre løsningene er at listen ville ha kommet ferdig sortert.

b) Det er oversiktig at det meste går gjennom Game, og det er praktisk å ha noen standardmetoder
 der, for eksempel for getPossibleMoves() som alle aktører kan bruke. Det blir også lettere å feilsøke.
 
* ...

# Del C
## Oversikt over designvalg og hva du har gjort

CX:
I del C har jeg laget en metode for å bytte kart slik at spilleren kan gå inn i nye rom. Jeg har laget 
en MonsterRabbit utvider Rabbit, men er mye sterkere og følger etter spilleren heller enn å lete etter
mat. Jeg har også laget hybelkaniner [DustRabbit](inf101.v18.rogue101.objects.DustBunny). De er like 
som Rabbit med unntak av at de spiser støv istedenfor IEdible-objekter. 

*C1: Ting som påvirker spillet:* 
Jeg laget en klasse [Door]inf101.v18.rogue101.objects.Door som utvider [Wall](inf101.v18.rogue101.objects.Wall),
men tegnes annerledes og kan brukes til å gå inn i et annet rom, forutsatt at spilleren har plukket 
opp en nøkkel ([Key](inf101.v18.rogue101.objects.Key)).

C3: Styling
Draw-metode for epler i klassen [Apple](inf101.v18.rogue101.objects.Apple). Den er basert på tegnemetoden 
i Carrot, men med andre farger og dimensjoner. Endret bakgrunnsfage og symboler.

*C5: Meldingsvindu*
Meldingsvinduet er implementert i [Game], se metoden displayMessage(). Jeg har eksperimentert litt med plasseringen 
til meldingsvinduet. Fordelen med å ha det under er at det da også vil virke i fullskjermsmodus, men 
til gjengjeld der er det bare plass til tre meldinger. Derfor har jeg valgt å ha meldingene på høyre 
side. 

*C6: Win condition
Spillet er vunnet hvis spilleren dreper monsterkaninen i det andre rommet. 

