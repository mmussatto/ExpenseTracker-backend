/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    // -------------- Constants ----------------------------
    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Color COLOR = Color.BLUE;
    public static final Transaction TRANSACTION = new Transaction();
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;


    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;



    @BeforeAll
    static void initializeTransaction() {
        TRANSACTION.setId(1);
    }


    // -------------- READ ----------------------------
    @Test
    void getAllTags() {

        Tag t1 = new Tag();
        t1.setId(1);

        Tag t2 = new Tag();
        t1.setId(2);

        List<Tag> tags = Arrays.asList(t1, t2);

        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> returnedList = tagService.getAllTags();

        assertEquals(tags.size(), returnedList.size());
    }

    @Test
    void getTagById() {

        Tag tag = createTagEntity();

        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));

        Tag returnedEntity = tagService.getTagById(tag.getId());

        assertEquals(tag.getId(), returnedEntity.getId());
        assertEquals(tag.getName(), returnedEntity.getName());
        assertEquals(tag.getColor(), returnedEntity.getColor());
        assertEquals(tag.getTransactions(), returnedEntity.getTransactions());
    }

    @Test
    void getTagById_NotFound() {

        when(tagRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(ID));
    }

    @Test
    void getTagByName() {

        Tag tag = createTagEntity();

        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));

        Tag returnedEntity = tagService.getTagByName(tag.getName());

        assertEquals(tag.getId(), returnedEntity.getId());
        assertEquals(tag.getName(), returnedEntity.getName());
        assertEquals(tag.getColor(), returnedEntity.getColor());
        assertEquals(tag.getTransactions(), returnedEntity.getTransactions());
    }

    @Test
    void getTagByName_NotFound() {

        when(tagRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagByName(NAME));
    }


    // -------------- CREATE ----------------------------
    @Test
    void createNewTag() {

        //Entity passed to function
        Tag passedEntity = new Tag(NAME, COLOR);

        //Saved Entity
        Tag savedTag = new Tag(passedEntity.getName(), passedEntity.getColor());
        savedTag.setId(ID);

        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(passedEntity)).thenReturn(savedTag);


        Tag returnedEntity = tagService.createNewTag(passedEntity);


        assertEquals(savedTag.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
    }

    @Test
    void createNewTag_NameAlreadyExists() {

        //Entity passed to function
        Tag passedEntity = new Tag(NAME, COLOR);
        passedEntity.getTransactions().add(TRANSACTION);

        //Saved Entity
        Tag savedTag = createTagEntity();

        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(savedTag));

        assertThrows(ResourceAlreadyExistsException.class, () -> tagService.createNewTag(passedEntity));
    }


    // -------------- UPDATE ----------------------------
    @Test
    void updateTagById() {

        //Entity passed to function
        Tag passedEntity = new Tag("Test Update", Color.GREEN);

        //Saved Entity
        Tag originalEntity = createTagEntity();

        //To update Tag
        Tag toUpdateEntity = new Tag(passedEntity.getName(), passedEntity.getColor());
        toUpdateEntity.setId(originalEntity.getId());

        //Updated Tag
        Tag updatedTag = new Tag(toUpdateEntity.getName(), toUpdateEntity.getColor());
        updatedTag.setId(toUpdateEntity.getId());
        updatedTag.setTransactions(originalEntity.getTransactions());

        when(tagRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(toUpdateEntity)).thenReturn(updatedTag);


        Tag returnedEntity = tagService.updateTagById(originalEntity.getId(), passedEntity);


        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(originalEntity.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, times(1)).findByName(passedEntity.getName());
        verify(tagRepository, times(1)).save(toUpdateEntity);
    }

    @Test
    void updateTagById_NotFound() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Update", Color.GREEN);

        //Saved Entity
        Tag originalEntity = createTagEntity();

        when(tagRepository.findById(originalEntity.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tagService.updateTagById(originalEntity.getId(), passedEntity));
    }

    @Test
    void updateTagById_NameAlreadyExists() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Saved Entity already using passedEntity name
        Tag nameAlreadyInUseEntity = new Tag(passedEntity.getName(), COLOR);


        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(nameAlreadyInUseEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> tagService.updateTagById(original.getId(), passedEntity));
    }


    // -------------- PATCH ----------------------------
    @Test
    void patchTagById() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Patch", Color.GREEN);

        //Saved Entity
        Tag originalEntity = createTagEntity();

        //Tag that will be updated and saved
        Tag updatedEntity = new Tag(passedEntity.getName(), passedEntity.getColor());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(tagRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updatedEntity)).thenReturn(updatedEntity);


        Tag returnedEntity = tagService.patchTagById(originalEntity.getId(), passedEntity);


        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(originalEntity.getTransactions(),returnedEntity.getTransactions());

        verify(tagRepository, times(1)).findByName(passedEntity.getName());
        verify(tagRepository, times(1)).save(updatedEntity);

    }

    @Test
    void patchTagById_UpdateOnlyName() {

        //DTO passed to function
        Tag passedEntity = new Tag();
        passedEntity.setName("Test Patch");
        //missing color

        //Saved Entity
        Tag originalEntity = createTagEntity();

        //Tag that will be updated and saved
        Tag updatedEntity = new Tag(passedEntity.getName(), originalEntity.getColor());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(tagRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updatedEntity)).thenReturn(updatedEntity);


        Tag returnedEntity = tagService.patchTagById(originalEntity.getId(), passedEntity);


        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(originalEntity.getColor(), returnedEntity.getColor());
        assertEquals(originalEntity.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, times(1)).findByName(passedEntity.getName());
        verify(tagRepository, times(1)).save(updatedEntity);

    }

    @Test
    void patchTagById_UpdateOnlyColor() {

        //DTO passed to function
        Tag passedEntity = new Tag();
        //missing name
        passedEntity.setColor(Color.RED);

        //Saved Entity
        Tag originalEntity = createTagEntity();

        //Tag that will be updated and saved
        Tag updatedEntity = new Tag(originalEntity.getName(), passedEntity.getColor());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(tagRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(tagRepository.save(updatedEntity)).thenReturn(updatedEntity);


        Tag returnedEntity = tagService.patchTagById(originalEntity.getId(), passedEntity);


        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(originalEntity.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, never()).findByName(passedEntity.getName());
        verify(tagRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchTagById_NotFound() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Patch", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class,
                () -> tagService.patchTagById(original.getId(), passedEntity));
    }

    @Test
    void patchTagById_NameAlreadyExists() {

        //DTO passed to function
        Tag passedEntity = new Tag();
        passedEntity.setName("Test Patch");

        //Saved Entity
        Tag original = createTagEntity();

        //Another Saved Entity with the passedEntity name
        Tag anotherSavedEntity = createTagEntity();
        anotherSavedEntity.setName(passedEntity.getName());


        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> tagService.patchTagById(original.getId(), passedEntity));
    }


    // -------------- DELETE ----------------------------
    @Test
    void deleteTagById() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(new Tag()));
        doNothing().when(tagRepository).deleteById(ID);

        tagService.deleteTagById(ID);

        verify(tagRepository, times(1)).deleteById(ID);
    }

    @Test
    void deleteTagById_NotFound() {

        Integer notFoundId = 123;

        when(tagRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tagService.deleteTagById(notFoundId));

        verify(tagRepository, times(1)).findById(notFoundId);
    }


    // -------------- TRANSACTIONS ----------------------------
    @Test
    void getTransactionsByTagId() {

        //Create transactions
        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        List<Transaction> transactions = Arrays.asList(t1, t2);

        //Create tag returned by the repository
        Tag tag = new Tag(NAME, COLOR);
        tag.setId(ID);
        t1.getTags().add(tag);
        t2.getTags().add(tag);
        tag.setTransactions(transactions);

        //Create page returned by the service
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());



        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));

        Page<Transaction> returnPagedTransactions = tagService.getTransactionsByTagId(tag.getId(), DEFAULT_PAGE, DEFAULT_SIZE);

        assertEquals(DEFAULT_SIZE, returnPagedTransactions.getContent().size(), "Wrong number of transactions");
        assertEquals(pagedTransactions, returnPagedTransactions);
    }

    @Test
    void getTransactionsByTagId_NotFound() {

        Integer notFoundId = 123;

        when(tagRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                tagService.getTransactionsByTagId(notFoundId, DEFAULT_PAGE, DEFAULT_SIZE));
    }


    // -------------- Helpers ----------------------------
    private static Tag createTagEntity() {
        Tag tag = new Tag(NAME, COLOR);
        tag.setId(ID);
        tag.getTransactions().add(TRANSACTION);
        return tag;
    }
}