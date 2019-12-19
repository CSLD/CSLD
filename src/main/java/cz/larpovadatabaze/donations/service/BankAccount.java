package cz.larpovadatabaze.donations.service;

import cz.larpovadatabaze.donations.model.Donation;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This service represents Bank Account and as such allows us to retrieve the information from the bank account. These information are then stored in the persistent store.
 * It needs to be run ever day at midnight. It will add all transactions from previous day
 */
public class BankAccount {
    private final static Logger logger = Logger.getLogger(BankAccount.class);
    private SessionFactory sessionFactory;
    private Timer timer;

    public BankAccount(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void start() {
        if(timer != null) {
            return;
        }

        long hour = 60 * 60 * 60 * 1000L;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Session current = sessionFactory.openSession();
                Transaction storeDonations = current.beginTransaction();
                importData(new DatabaseDonations(current), current);
                storeDonations.commit();
                current.close();
            }
        }, 0, hour);
    }


    void importData(Donations donations, Session current) {
        try {
            logger.debug("BANK: Loading Data.");
            Document doc = Jsoup.connect("https://www.fio.cz/ib2/transparent?a=2300942293").get();
            Elements linesToParse = doc.select("table.table tbody tr");
            for (Element element : linesToParse) {
                Elements cells = element.select("td");
                if (cells.size() < 9) {
                    continue;
                }
                String dateText = cells.get(0).text();
                String priceText = cells.get(1).attr("data-value");
                if(priceText.startsWith("-")) {
                    continue;
                }

                Double price;
                Date date;
                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(dateText);
                    price = Double.parseDouble(priceText);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String donor = cells.get(8).text();
                // Verify whether this transaction is already in the database. If no create donation and insert into database.
                if(new FilteredDonations(current, donor, date, price).all().size() == 0) {
                    donations.store(new Donation(date, price, donor, donor));
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
