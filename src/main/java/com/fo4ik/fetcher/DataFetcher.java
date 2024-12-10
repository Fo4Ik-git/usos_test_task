package com.fo4ik.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataFetcher {

    private static int GET_LIMIT;
    private static int LIMIT;
    private static int OFFSET = 0;
    private static String BASE_URL;

    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/602.3.12 (KHTML, like Gecko) Version/10.0.3 Safari/602.3.12",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1"
    };


    static {
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            BASE_URL = prop.getProperty("BASE_URL");
            GET_LIMIT = Integer.parseInt(prop.getProperty("GET_LIMIT"));
            LIMIT = Integer.parseInt(prop.getProperty("LIMIT"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getRandomUserAgent() {
        Random random = new Random();
        return USER_AGENTS[random.nextInt(USER_AGENTS.length)];
    }

    public void fetchData() {
        while (OFFSET < LIMIT ) {
            String url = BASE_URL + "&tab0350_offset=" + OFFSET + "&tab0350_limit=" + GET_LIMIT;
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Data> dataList = new ArrayList<>();

            try(ProgressBar pb = new ProgressBarBuilder()
                    .setTaskName("Processing Links")
                    .setInitialMax(GET_LIMIT)
                    .setStyle(ProgressBarStyle.ASCII)
                    .build()) {
                Document doc = Jsoup.connect(url)
                        .userAgent(getRandomUserAgent())
                        .get();
                Elements elements = doc.select("usos-link");


                for (Element element : elements) {
                    String link = element.select("a").attr("href");
                    executor.submit(() -> {
                        try {
                            Document linkDoc = Jsoup.connect(link)
                                    .userAgent(getRandomUserAgent())
                                    .get();
                            String nazwaPrzedmiotu = "";
                            String prowadzacyGrup = "";

                            Elements rows = linkDoc.select("tr");
                            for (Element row : rows) {
                                Elements tds = row.select("td");
                                if (tds.size() > 1) {
                                    if (tds.get(0).text().equals("Nazwa przedmiotu:")) {
                                        nazwaPrzedmiotu = tds.get(1).text();
                                    } else if (tds.get(0).text().equals("Prowadzący grup:")) {
                                        prowadzacyGrup = tds.get(1).text();
                                    }
                                }
                            }

                            synchronized (dataList) {
                                dataList.add(new Data(link, nazwaPrzedmiotu, prowadzacyGrup));
                            }
                            pb.step();
                        } catch (IOException e) {
                            System.err.println("Error processing link: " + link);
                            e.printStackTrace();
                        }
                    });
                }

                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.HOURS);

                saveDataToJson(dataList);


                OFFSET += GET_LIMIT;
            } catch (IOException | InterruptedException e) {
                System.err.println("Error fetching data.");
                e.printStackTrace();
            }
        }

        System.out.println("Data saved to JSON.");
        OFFSET = 0;
    }

    private void saveDataToJson(List<Data> dataList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("data.json")) {
            gson.toJson(dataList, writer);
        } catch (IOException e) {
            System.err.println("Error saving data to JSON.");
            e.printStackTrace();
        }
    }

    private static class Data {
        private String link;
        private String nazwaPrzedmiotu;
        private String prowadzacyGrup;

        public Data(String link, String nazwaPrzedmiotu, String prowadzacyGrup) {
            this.link = link;
            this.nazwaPrzedmiotu = nazwaPrzedmiotu;
            this.prowadzacyGrup = prowadzacyGrup;
        }
    }

}
