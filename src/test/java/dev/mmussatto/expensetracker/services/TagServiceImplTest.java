/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Tag;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.TagRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    TagService tagService;

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Color COLOR = Color.BLUE;
    public static final Transaction TRANSACTION = new Transaction();

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository);
        TRANSACTION.setId(1);
    }

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

    @Test
    void createNewTag() {

        //Entity passed to function
        Tag passedEntity = new Tag(NAME, COLOR);
        passedEntity.getTransactions().add(TRANSACTION);

        //Saved Entity
        Tag savedTag = new Tag(passedEntity.getName(), passedEntity.getColor());
        savedTag.setTransactions(passedEntity.getTransactions());
        savedTag.setId(ID);

        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(passedEntity)).thenReturn(savedTag);


        Tag returnedEntity = tagService.createNewTag(passedEntity);


        assertEquals(savedTag.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(passedEntity.getTransactions(), returnedEntity.getTransactions());
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

    @Test
    void updateTagById() {

        //Entity passed to function
        Tag passedEntity = new Tag("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Updated Tag
        Tag updatedTag = new Tag(passedEntity.getName(), passedEntity.getColor());
        updatedTag.setId(original.getId());
        updatedTag.setTransactions(passedEntity.getTransactions());

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updatedTag)).thenReturn(updatedTag);


        Tag returnedEntity = tagService.updateTagById(original.getId(), passedEntity);


        assertEquals(original.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(passedEntity.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, times(1)).findByName(passedEntity.getName());
    }

    @Test
    void updateTagById_NotFound() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tagService.updateTagById(original.getId(), passedEntity));
    }

    @Test
    void updateTagById_NameAlreadyExists() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Saved Entity already using passedEntity name
        Tag anotherSavedEntity = createTagEntity();
        anotherSavedEntity.setName("Test Update");


        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> tagService.updateTagById(original.getId(), passedEntity));
    }

    @Test
    void patchTagById() {

        //DTO passed to function
        Tag passedEntity = new Tag("Test Patch", Color.GREEN);
        passedEntity.getTransactions().add(TRANSACTION);

        //Saved Entity
        Tag original = new Tag(NAME, COLOR);
        original.setId(ID);

        //Tag that will be updated and saved
        Tag updated = new Tag(passedEntity.getName(), passedEntity.getColor());
        updated.setId(original.getId());
        updated.setTransactions(passedEntity.getTransactions());

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updated)).thenReturn(updated);


        Tag returnedEntity = tagService.patchTagById(original.getId(), passedEntity);


        assertEquals(original.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(original.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, times(1)).findByName(passedEntity.getName());
    }

    @Test
    void patchTagById_UpdateOnlyName() {

        //DTO passed to function
        Tag passedEntity = new Tag();
        passedEntity.setName("Test Patch");

        //Saved Entity
        Tag original = createTagEntity();

        //Tag that will be updated and saved
        Tag updated = new Tag(passedEntity.getName(), original.getColor());
        updated.setId(original.getId());
        updated.setTransactions(original.getTransactions());

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updated)).thenReturn(updated);


        Tag returnedEntity = tagService.patchTagById(original.getId(), passedEntity);


        assertEquals(original.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(original.getColor(), returnedEntity.getColor());
        assertEquals(original.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, times(1)).findByName(passedEntity.getName());
    }

    @Test
    void patchTagById_UpdateOnlyColor() {

        //DTO passed to function
        Tag passedEntity = new Tag();
        passedEntity.setColor(Color.RED);

        //Saved Entity
        Tag original = createTagEntity();

        //Tag that will be updated and saved
        Tag updated = new Tag(original.getName(), passedEntity.getColor());
        updated.setId(original.getId());
        updated.setTransactions(original.getTransactions());

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.save(updated)).thenReturn(updated);


        Tag returnedEntity = tagService.patchTagById(original.getId(), passedEntity);


        assertEquals(original.getId(), returnedEntity.getId());
        assertEquals(original.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(original.getTransactions(), returnedEntity.getTransactions());

        verify(tagRepository, never()).findByName(passedEntity.getName());
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
        anotherSavedEntity.setName("Test Patch");


        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> tagService.patchTagById(original.getId(), passedEntity));
    }

    @Test
    void deleteTagById() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(new Tag()));
        doNothing().when(tagRepository).deleteById(ID);

        tagService.deleteTagById(ID);

        verify(tagRepository, times(1)).deleteById(ID);
    }

    @Test
    void deleteTagById_NotFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(new Tag()));
        doNothing().when(tagRepository).deleteById(ID);

        tagService.deleteTagById(ID);

        verify(tagRepository, times(1)).deleteById(ID);
    }

    @Test
    void getTagTransactionsById() {

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        Set<Transaction> transactions = new HashSet<>(Arrays.asList(t1, t2));

        Tag tag = new Tag(NAME, COLOR);
        tag.setId(ID);
        t1.getTags().add(tag);
        t2.getTags().add(tag);
        tag.setTransactions(transactions);

        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));

        Set<Transaction> returnedSet = tagService.getTagTransactionsById(tag.getId());

        assertEquals(transactions, returnedSet);
    }

    private static Tag createTagEntity() {
        Tag tag = new Tag(NAME, COLOR);
        tag.setId(ID);
        tag.getTransactions().add(TRANSACTION);
        return tag;
    }
}