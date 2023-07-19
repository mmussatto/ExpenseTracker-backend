/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.DataLoader;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.category.CategoryRepository;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethodRepository;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.tag.TagRepository;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionRepository;
import dev.mmussatto.expensetracker.entities.vendor.VendorRepository;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(
        name = {"spring.jpa.hibernate.ddl-auto"},
        havingValue = "create")
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final VendorRepository<OnlineStore> onlineStoreStoreRepository;
    private final VendorRepository<PhysicalStore> physicalStoreStoreRepository;
    private final TagRepository tagRepository;
    private final TransactionRepository transactionRepository;

    public DataLoader(CategoryRepository categoryRepository,
                      PaymentMethodRepository paymentMethodRepository,
                      VendorRepository<OnlineStore> onlineStoreStoreRepository,
                      VendorRepository<PhysicalStore> physicalStoreStoreRepository,
                      TagRepository tagRepository,
                      TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.onlineStoreStoreRepository = onlineStoreStoreRepository;
        this.physicalStoreStoreRepository = physicalStoreStoreRepository;
        this.tagRepository = tagRepository;
        this.transactionRepository = transactionRepository;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        LoadCategories();
        LoadPaymentMethods();
        LoadStores();
        LoadTags();
        LoadTransactions();
    }


    private void LoadCategories() {
        Category c1 = new Category("Recreation", Color.BLUE);
        Category c2 = new Category("Games", Color.GREEN);
        Category c3 = new Category("Food", Color.RED);
        Category c4 = new Category("School", Color.RED);

        categoryRepository.save(c1);
        categoryRepository.save(c2);
        categoryRepository.save(c3);
        categoryRepository.save(c4);


    }


    private void LoadPaymentMethods() {
        PaymentMethod pm1 = new PaymentMethod("Nubank", PaymentType.CREDIT_CARD);
        PaymentMethod pm2 = new PaymentMethod("Peter Parker", PaymentType.PIX);
        PaymentMethod pm3 = new PaymentMethod("DebNubank", PaymentType.DEBIT_CARD);

        paymentMethodRepository.save(pm1);
        paymentMethodRepository.save(pm2);
        paymentMethodRepository.save(pm3);

    }


    private void LoadStores() {
        OnlineStore os1 = new OnlineStore("PlaystationStore", "https://store.playstation.com");
        OnlineStore os2 = new OnlineStore("IFood", "https://www.ifood.com.br/");
        OnlineStore os3 = new OnlineStore("Steam", "https://steam.com");

        onlineStoreStoreRepository.saveAll(Arrays.asList(os1, os2, os3));

        PhysicalStore ps1 = new PhysicalStore("McDonald's", "Av. Mogi Mirim, 1515");
        PhysicalStore ps2 = new PhysicalStore("Cinemark", "Av. Big Bom, 1616");

        physicalStoreStoreRepository.saveAll(Arrays.asList(ps1, ps2));

    }


    private void LoadTags() {
        Tag t1 = new Tag("Movies", Color.RED);
        Tag t2 = new Tag("Happy", Color.GREEN);
        Tag t3 = new Tag("Videogames", Color.BLUE);

        tagRepository.saveAll(Arrays.asList(t1, t2, t3));
    }


    private void LoadTransactions() {

        //Retrieve Resources
        Category gamesCategory = categoryRepository.findByName("Games")
                .orElseThrow(() -> new RuntimeException("Expected Category not found"));

        Category recreationCategory = categoryRepository.findByName("Recreation")
                .orElseThrow(() -> new RuntimeException("Expected Category not found"));

        Category foodCategory = categoryRepository.findByName("Food")
                .orElseThrow(() -> new RuntimeException("Expected Category not found"));

        PaymentMethod nubank = paymentMethodRepository.findByName("Nubank")
                .orElseThrow(() -> new RuntimeException("Expected Payment Method not found"));

        PaymentMethod debNubank = paymentMethodRepository.findByName("DebNubank")
                .orElseThrow(() -> new RuntimeException("Expected Payment Method not found"));

        OnlineStore playstationStore = onlineStoreStoreRepository.findByName("PlaystationStore")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));

        OnlineStore steam = onlineStoreStoreRepository.findByName("Steam")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));

        OnlineStore ifood = onlineStoreStoreRepository.findByName("IFood")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));

        PhysicalStore cinemark = physicalStoreStoreRepository.findByName("Cinemark")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));

        PhysicalStore mcDonalds = physicalStoreStoreRepository.findByName("McDonald's")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));


        Tag videogamesTag = tagRepository.findByName("Videogames")
                .orElseThrow(() -> new RuntimeException("Expected Tag not found"));

        Tag happyTag = tagRepository.findByName("Happy")
                .orElseThrow(() -> new RuntimeException("Expected Tag not found"));

        Tag moviesTag = tagRepository.findByName("Movies")
                .orElseThrow(() -> new RuntimeException("Expected Tag not found"));

        Set<Tag> tagSet = Stream.of(videogamesTag, happyTag).collect(Collectors.toSet());


        //Games
        Transaction t_games1 = new Transaction(200.00, LocalDateTime.now().withNano(0),
                "God of War Ragnarok", gamesCategory, nubank, playstationStore, tagSet );

        Transaction t_games2 = new Transaction(250.00, LocalDateTime.of(2023, 6, 10, 8, 30, 30).withNano(0),
                "Satisfactory", gamesCategory, nubank, steam, tagSet );

        Transaction t_games3 = new Transaction(19.00, LocalDateTime.now().withNano(0),
                "Deep Rock Galactic", gamesCategory, debNubank, steam, tagSet );

        Transaction t_games4 = new Transaction(50.00, LocalDateTime.now().withNano(0),
                "Hades", gamesCategory, nubank, steam, tagSet );

        Transaction t_games5 = new Transaction(200.00, LocalDateTime.of(2012, 4, 24, 8, 30, 30).withNano(0),
                "COD: Black Ops II", gamesCategory, nubank, playstationStore, tagSet );

        Transaction t_games6 = new Transaction(300.00, LocalDateTime.now().withNano(0),
                "Borderlands 3", gamesCategory, nubank, playstationStore, Stream.of(videogamesTag).collect(Collectors.toSet()) );

        Transaction t_games7 = new Transaction(45.00, LocalDateTime.of(2023, 5, 15, 8, 30, 30).withNano(0),
                "Inscryption", gamesCategory, debNubank, steam, tagSet );

        Transaction t_games8 = new Transaction(4.00, LocalDateTime.now().withNano(0),
                "FTL: Faster than Light", gamesCategory, nubank, steam, tagSet );

        Transaction t_games9 = new Transaction(100.00, LocalDateTime.of(2021, 4, 24, 8, 30, 30).withNano(0),
                "Civilization VI", gamesCategory, nubank, steam, tagSet );



        //Movies
        Transaction t_movies1 = new Transaction(10.76, LocalDateTime.of(2023, 4, 24, 8, 30, 30).withNano(0),
                "Avatar", recreationCategory, debNubank, cinemark, Stream.of(moviesTag).collect(Collectors.toSet()) );

        Transaction t_movies2 = new Transaction(13.00, LocalDateTime.of(2023, 10, 3, 15, 45, 30).withNano(0),
                "Top Gun: Maverick", recreationCategory, debNubank, cinemark, Stream.of(moviesTag, happyTag).collect(Collectors.toSet()) );

        Transaction t_movies3 = new Transaction(20.00, LocalDateTime.of(2023, 7, 20, 15, 45, 30).withNano(0),
                "Barbie", recreationCategory, debNubank, cinemark, Stream.of(moviesTag, happyTag).collect(Collectors.toSet()) );

        Transaction t_movies4 = new Transaction(20.00, LocalDateTime.of(2023, 7, 20, 15, 45, 30).withNano(0),
                "Oppenheimer", recreationCategory, nubank, cinemark, Stream.of(moviesTag).collect(Collectors.toSet()) );

        Transaction t_movies5 = new Transaction(45.00, LocalDateTime.of(2023, 7, 15, 15, 45, 30).withNano(0),
                "Spider-man Into the Spiderverse", recreationCategory, debNubank, cinemark, Stream.of(moviesTag, happyTag).collect(Collectors.toSet()) );

        Transaction t_movies6 = new Transaction(10.76, LocalDateTime.of(2004, 12, 10, 15, 45, 30).withNano(0),
                "The Incredibles", recreationCategory, debNubank, cinemark, Stream.of(moviesTag, happyTag).collect(Collectors.toSet()) );


        //Food
        Transaction t_food1 = new Transaction(60.00, LocalDateTime.now().withNano(0),
                "Big Mac", foodCategory, debNubank, mcDonalds , Stream.of(happyTag).collect(Collectors.toSet()) );

        Transaction t_food2 = new Transaction(60.00, LocalDateTime.of(2023, 4, 24, 8, 30, 30).withNano(0),
                "Big Mac", foodCategory, nubank, mcDonalds , Stream.of(happyTag).collect(Collectors.toSet()) );

        Transaction t_food3 = new Transaction(60.00, LocalDateTime.now().withNano(0),
                "El Cardal", foodCategory, nubank, ifood , Stream.of(happyTag).collect(Collectors.toSet()) );

        Transaction t_food4 = new Transaction(60.00, LocalDateTime.now().withNano(0),
                "Burger King", foodCategory, debNubank, ifood, Stream.of(happyTag).collect(Collectors.toSet()) );


        transactionRepository.saveAll(Arrays.asList(t_games1, t_games2, t_games3, t_games4, t_games5, t_games6, t_games7, t_games8, t_games9,
                t_movies1, t_movies2, t_movies3, t_movies4, t_movies5, t_movies6, t_food1, t_food2, t_food3, t_food4));

    }
}
