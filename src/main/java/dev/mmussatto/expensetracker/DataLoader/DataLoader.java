/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.DataLoader;

import dev.mmussatto.expensetracker.domain.*;
import dev.mmussatto.expensetracker.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final StoreRepository<OnlineStore> onlineStoreStoreRepository;
    private final StoreRepository<PhysicalStore> physicalStoreStoreRepository;
    private final TagRepository tagRepository;
    private final TransactionRepository transactionRepository;

    public DataLoader(CategoryRepository categoryRepository,
                      PaymentMethodRepository paymentMethodRepository,
                      StoreRepository<OnlineStore> onlineStoreStoreRepository,
                      StoreRepository<PhysicalStore> physicalStoreStoreRepository,
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

        categoryRepository.save(c1);
        categoryRepository.save(c2);
        categoryRepository.save(c3);

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

        onlineStoreStoreRepository.saveAll(Arrays.asList(os1, os2));

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

        PaymentMethod nubank = paymentMethodRepository.findByName("Nubank")
                .orElseThrow(() -> new RuntimeException("Expected Payment Method not found"));

        PaymentMethod debNubank = paymentMethodRepository.findByName("DebNubank")
                .orElseThrow(() -> new RuntimeException("Expected Payment Method not found"));

        OnlineStore playstationStore = onlineStoreStoreRepository.findByName("PlaystationStore")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));

        PhysicalStore cinemark = physicalStoreStoreRepository.findByName("Cinemark")
                .orElseThrow(() -> new RuntimeException("Expected Store not found"));

        Tag videogamesTag = tagRepository.findByName("Videogames")
                .orElseThrow(() -> new RuntimeException("Expected Tag not found"));

        Tag happyTag = tagRepository.findByName("Happy")
                .orElseThrow(() -> new RuntimeException("Expected Tag not found"));

        Tag moviesTag = tagRepository.findByName("Movies")
                .orElseThrow(() -> new RuntimeException("Expected Tag not found"));

        Set<Tag> tagSet = Stream.of(videogamesTag, happyTag).collect(Collectors.toSet());



        Transaction t1 = new Transaction(200.00, new Timestamp(new Date().getTime()),
                "God of War Ragnarok", nubank, gamesCategory, tagSet, playstationStore );
        nubank.getTransactions().add(t1);
        gamesCategory.getTransactions().add(t1);
        tagSet.forEach(tag -> tag.getTransactions().add(t1));
        playstationStore.getTransactions().add(t1);

        Transaction t2 = new Transaction(10.76, new Timestamp(new Date().getTime()), "Avatar",
                debNubank, recreationCategory, Stream.of(moviesTag).collect(Collectors.toSet()), cinemark );
        debNubank.getTransactions().add(t2);
        recreationCategory.getTransactions().add(t2);
        moviesTag.getTransactions().add(t2);
        cinemark.getTransactions().add(t2);

        transactionRepository.saveAll(Arrays.asList(t1, t2));

    }
}
