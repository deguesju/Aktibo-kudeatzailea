# Aktiboen Kudeatzailea

Android aplikazio sinple bat, kriptomoneta inbertsioen portfolio pertsonalizatuak sortu eta kudeatzeko.

## Funtzionalitate Nagusiak

- **Aktiboen Hautaketa:** Erabiltzaileak Bitget API-tik lortutako kriptomoneta zerrenda batetik (Bitcoin, Ethereum, Solana, etab.) aktiboak hauta ditzake.
- **Banaketa Sortzea:** Hautatutako aktiboekin, "banaketa" edo portfolio berri bat sortzen da.
- **Datuen Bistaratzea:** API-tik jasotako datuak (prezioa, ehuneko aldaketa) interfazean erakusten dira.
- **Datuen Kudeaketa:** Sortutako banaketak memorian gordetzen dira saioan zehar (`DistributionStore`).

## Erabilitako Teknologiak

- **Lengoaia:** Kotlin
- **Arkitektura:** MVVM patroiaren oinarriak
- **Sare-deiak:** Retrofit liburutegia Bitget API-arekin komunikatzeko.
- **Asinkronia:** Kotlin Coroutines atzeko planoko atazak kudeatzeko.
- **Interfazea:** Android SDK, RecyclerView zerrendak erakusteko.

## Nola Funtzionatzen Du?

Aplikazioa abiaraztean, `SelectAssetsActivity`-k API-ari deitzen dio aktiboen prezio eguneratuak lortzeko. Erabiltzaileak nahi dituenak aukeratu eta "Berretsi" botoia sakatzean, `Distribution` objektu berri bat sortu eta `DistributionActivity` pantailara nabigatzen da.
