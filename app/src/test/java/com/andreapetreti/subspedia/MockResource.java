package com.andreapetreti.subspedia;

public class MockResource {

    static final String MOCK_SERIES = "[\n" +
            "  {\n" +
            "    \"id_serie\": 504,\n" +
            "    \"nome_serie\": \"Camping\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/504/camping\",\n" +
            "    \"id_thetvdb\": 349942,\n" +
            "    \"stato\": \"In corso\",\n" +
            "    \"anno\": 2018\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 321,\n" +
            "    \"nome_serie\": \"The Resident\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/321/the-resident\",\n" +
            "    \"id_thetvdb\": 328569,\n" +
            "    \"stato\": \"In corso\",\n" +
            "    \"anno\": 2018\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 423,\n" +
            "    \"nome_serie\": \"The Deuce\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/423/the-deuce\",\n" +
            "    \"id_thetvdb\": 317728,\n" +
            "    \"stato\": \"Rinnovata\",\n" +
            "    \"anno\": 2017\n" +
            "  }\n" +
            "]";

    static final String MOCK_SUBTITLES = "[\n" +
            "  {\n" +
            "    \"id_serie\": 504,\n" +
            "    \"nome_serie\": \"Camping\",\n" +
            "    \"ep_titolo\": \"Fishing Trip\",\n" +
            "    \"num_stagione\": 1,\n" +
            "    \"num_episodio\": 3,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13838.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/504/camping/episodio/1/3\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/504/camping\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13838\",\n" +
            "    \"descrizione\": \"Buona visione!\",\n" +
            "    \"id_thetvdb\": 349942,\n" +
            "    \"data_uscita\": \"2018-10-30 19:13:38\",\n" +
            "    \"grazie\": 8\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 321,\n" +
            "    \"nome_serie\": \"The Resident\",\n" +
            "    \"ep_titolo\": \"Nightmare\",\n" +
            "    \"num_stagione\": 2,\n" +
            "    \"num_episodio\": 6,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13837.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/321/the-resident/episodio/2/6\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/321/the-resident\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13837\",\n" +
            "    \"descrizione\": \"La protagonista del triangolino di questa settimana è Mina e... Che ci fa conciata così? Scopriamolo insieme in una puntata tutta dedica ad Halloween :)\",\n" +
            "    \"id_thetvdb\": 328569,\n" +
            "    \"data_uscita\": \"2018-10-30 18:39:08\",\n" +
            "    \"grazie\": 32\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 452,\n" +
            "    \"nome_serie\": \"9-1-1\",\n" +
            "    \"ep_titolo\": \"Haunted\",\n" +
            "    \"num_stagione\": 2,\n" +
            "    \"num_episodio\": 7,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13833.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/452/9-1-1/episodio/2/7\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/452/9-1-1\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13833\",\n" +
            "    \"descrizione\": \"L'anno scorso abbiamo visto i casi di una notte di luna piena, ma cosa capiterà ad Halloween?\",\n" +
            "    \"id_thetvdb\": 337907,\n" +
            "    \"data_uscita\": \"2018-10-30 16:10:48\",\n" +
            "    \"grazie\": 45\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 493,\n" +
            "    \"nome_serie\": \"Kidding\",\n" +
            "    \"ep_titolo\": \"Philliam\",\n" +
            "    \"num_stagione\": 1,\n" +
            "    \"num_episodio\": 8,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13829.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/493/kidding/episodio/1/8\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/493/kidding\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13829\",\n" +
            "    \"descrizione\": \"In questa puntata conosciamo il passato di Derrell e di tutta la famiglia Pickles. Buona visione!\",\n" +
            "    \"id_thetvdb\": 348841,\n" +
            "    \"data_uscita\": \"2018-10-30 14:41:54\",\n" +
            "    \"grazie\": 14\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 417,\n" +
            "    \"nome_serie\": \"DuckTales\",\n" +
            "    \"ep_titolo\": \"The Depths of Cousin Fethry!\",\n" +
            "    \"num_stagione\": 2,\n" +
            "    \"num_episodio\": 2,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13828.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/417/ducktales/episodio/2/2\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/417/ducktales\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13828\",\n" +
            "    \"descrizione\": \"In questo episodio spunterà un'altra vecchia conoscenza, correte a vedere l'episodio per scoprire chi!\",\n" +
            "    \"id_thetvdb\": 330134,\n" +
            "    \"data_uscita\": \"2018-10-30 11:42:40\",\n" +
            "    \"grazie\": 17\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 423,\n" +
            "    \"nome_serie\": \"The Deuce\",\n" +
            "    \"ep_titolo\": \"Nobody Has To Get Hurt\",\n" +
            "    \"num_stagione\": 2,\n" +
            "    \"num_episodio\": 8,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13827.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/423/the-deuce/episodio/2/8\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/423/the-deuce\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13827\",\n" +
            "    \"descrizione\": \"Un finale inaspettato :D Buona visione e appuntamento a lunedì prossimo col finale di stagione!\",\n" +
            "    \"id_thetvdb\": 317728,\n" +
            "    \"data_uscita\": \"2018-10-29 22:38:50\",\n" +
            "    \"grazie\": 41\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 491,\n" +
            "    \"nome_serie\": \"You\",\n" +
            "    \"ep_titolo\": \"You Got me Babe\",\n" +
            "    \"num_stagione\": 1,\n" +
            "    \"num_episodio\": 8,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13826.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/491/you/episodio/1/8\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/491/you\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13826\",\n" +
            "    \"descrizione\": \"Joe e Beck si rimetteranno insieme?\",\n" +
            "    \"id_thetvdb\": 336924,\n" +
            "    \"data_uscita\": \"2018-10-29 17:40:11\",\n" +
            "    \"grazie\": 49\n" +
            "  },\n" +
            "  {\n" +
            "    \"id_serie\": 177,\n" +
            "    \"nome_serie\": \"Z Nation\",\n" +
            "    \"ep_titolo\": \"Pacifica\",\n" +
            "    \"num_stagione\": 5,\n" +
            "    \"num_episodio\": 4,\n" +
            "    \"immagine\": \"https://www.subspedia.tv/immagini/triangoli_episodi/13816.png\",\n" +
            "    \"link_sottotitoli\": \"https://www.subspedia.tv/serie/177/z-nation/episodio/5/4\",\n" +
            "    \"link_serie\": \"https://www.subspedia.tv/serie/177/z-nation\",\n" +
            "    \"link_file\": \"https://www.subspedia.tv/download/episodio/13816\",\n" +
            "    \"descrizione\": \"Pronti a scoprire cosa si nasconde a Pacifica?\",\n" +
            "    \"id_thetvdb\": 280494,\n" +
            "    \"data_uscita\": \"2018-10-28 14:04:49\",\n" +
            "    \"grazie\": 38\n" +
            "  }\n" +
            "]";
}
