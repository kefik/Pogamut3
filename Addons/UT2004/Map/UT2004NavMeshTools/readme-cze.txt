Ahoj,

ok, tak když budeš chtít cokoliv vysvìtlit tak se ozvi. Pøípadnì kdybys chtìl doprogramovat nìjaký konkrétní mìnší celek, tak ti s tím mùžu pomoct tak že bych to dìlal sám tøeba nìjak pøes víkend, to bychom se v tom pøípadì ještì domluvili.

Získání navmeshe má 3 èásti:

1. dostat z mapy (.ut2) geometrii (.xml)
2. pøeformátovat geometrii do formátu použitelného pro Recast (.obj)
3. získat navmesh z Recatsu (.navmesh)

Jak to provést:

1. Staèí spustit mnou upravený UShock na vybranou mapu. Je to command-line utilita, takže napøíklad napíšeš do cmd "UShock.exe DM-1-on-1-Albatross.ut2" a výsledná geometrie se uloží do adresáøe output vedle binárky UShocku. Ten adresáø output tam musí být pøedem, automaticky se nevytvoøí myslím. Pro hromadné vyextrahování všech map jsem napsal batch skript bulk_transform.bat, který staèí spustit dvojklikem, a on pøevede všechny mapy, které najde v adresáøi map UT2004. Cesta k tomuto adresáøi je napsaná uvnítø skriptu a na mém poèítaèi je to "C:\Program Files\GOG.com\Unreal Tournament 2004\maps". To si pøípadnì musíš, pøepsat, pokud jsou mapy jinde.

UShock.exe i bulk_transform.bat jsou na CD v Attachments\03-AlteredUShock\win32_binary. O adresáø vedle jsou pak zdrojáky UShocku, pokud bys ho chtìl upravovat.

2. Pøedpokládáme, že máš adresáø s hromadou .xml filù. V tomhle kroku se formát dat jenom pøevede z XML na nìco jako CSV s mezerami. Navíc se geometrie vycentruje, aby mìla støed v [0;0;0] a zvìtší / zmenší, aby její nejdelší rozmìr sahal od -100 do +100. Tento pøevod provádí java tøída UShock2Recast, respektive její metoda main. Na vstupu bere 2 parametry: adresáø, kde má hledat geometrii z UShocku a název souboru s geometríí, který má zpracovat.
Èili v pøíkazové øádce by to mìlo jít spustit napø. pøíkazem:
java UShock2Recast D:\navMesh\UShock\output DM-1-on-1-Albatross.xml
Opìt je možné pøevést v šechny mapy najednou a k tomuto úèelu existuje skript skript.bat, který tøídu spustí na všechny soubory, které najde v zadaném adresáøi, který je v nìm napsaný. V mém pøípadì je to D:\navMesh\UShock\moje-binarka\output

Bouhžel z nìjakého dùvodu mi na mém poèítaèi nefunguje spuštìní tohoto pøíkazu z pøíkazové øádky a proto když chci tento krok provést, tak si projekt UT2004LevelGeom otevøu v Netbeansech, nastavím dva zmínìné  vstupní parametry v properties projektu a spustím tøídu  UShock2Recast tam, pøípadnì MultiUShock2Recast pro pøevod všech map v požadovaném adresáøi. MultiUShock2Recast bere jediný parametr a to název adresáøe, ve kterém má hledat soubory. Na každý z nich pa spustí obyèejný UShock2Recast.

Na výstupu (opìt adersáø output) tedy dostaneš .obj soubory a ke každému ještì .scale a .centre soubor, kde jsou informace o transformaci souøadnic, které bude v následujícím kroku potøeboat Recast.

Koøen tohoto Netbeans projektu je v Attachments\05-
AlteredUT2004LevelGeom\UT2004LevelGeom.

3. Pøedpokládáme, že máš adresáø s hromadou .obj, . scale a .centre filù.
Pøevod na .navmesh provede Recast. Staèí spustit Recast s jediným parametrem - název mapy.obj. Recast si už pak sám vyhledá soubory s pøíponami .scale a .centre (musí existovat). Všechny vstupy Recast hledá v adresáøi Meshes (relativnì vedle binárky) a výstupy ukládá do adresáøe output (relativnì vedle binárky, musí existovat).

Pro pøíklad exsitují na CD skripty run.bat (spustí pøeklad pro mapu DM-Flux)
a runAll.bat (spustí pøekald pro všechny mapy)

Všechny tyto soubory najdeš v Attachments\07-AlteredRecast\recastnavigation-read-only\RecastDemo\Bin.
Zdrojáky jsou jako vždy o adresáø vedle. Recast je o dost pomalejší než UShock a pøeložit všechny mapy mi trvalo nìkolik hodin.