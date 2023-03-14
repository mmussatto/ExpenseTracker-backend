/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.TagMapper;
import dev.mmussatto.expensetracker.api.model.TagDTO;
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
        tagService = new TagServiceImpl(TagMapper.INSTANCE, tagRepository);
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

        List<TagDTO> returnedList = tagService.getAllTags();

        assertEquals(tags.size(), returnedList.size());
        assertEquals("/api/tags/" + t1.getId(), returnedList.get(0).getPath());
        assertEquals("/api/tags/" + t2.getId(), returnedList.get(1).getPath());

    }

    @Test
    void getTagById() {

        Tag tag = createTagEntity();

        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));

        TagDTO tagDTO = tagService.getTagById(tag.getId());

        assertEquals(tag.getId(), tagDTO.getId());
        assertEquals(tag.getName(), tagDTO.getName());
        assertEquals(tag.getColor(), tagDTO.getColor());
        assertEquals(tag.getTransactions(), tagDTO.getTransactions());
        assertEquals("/api/tags/" + tag.getId() , tagDTO.getPath());
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

        TagDTO tagDTO = tagService.getTagByName(tag.getName());

        assertEquals(tag.getId(), tagDTO.getId());
        assertEquals(tag.getName(), tagDTO.getName());
        assertEquals(tag.getColor(), tagDTO.getColor());
        assertEquals(tag.getTransactions(), tagDTO.getTransactions());
        assertEquals("/api/tags/" + tag.getId() , tagDTO.getPath());
    }

    @Test
    void getTagByName_NotFound() {

        when(tagRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagByName(NAME));
    }

    @Test
    void createNewTag() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO(NAME, COLOR);
        passedDTO.getTransactions().add(TRANSACTION);

        //Tag to be saved
        Tag tagToBeSaved = new Tag(NAME, COLOR);
        tagToBeSaved.getTransactions().add(TRANSACTION);

        //Saved Entity
        Tag savedTag = createTagEntity();

        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(tagToBeSaved)).thenReturn(savedTag);


        TagDTO returnedDTO = tagService.createNewTag(passedDTO);


        assertEquals(savedTag.getId(), returnedDTO.getId());
        assertEquals(passedDTO.getName(), returnedDTO.getName());
        assertEquals(passedDTO.getColor(), returnedDTO.getColor());
        assertEquals(passedDTO.getTransactions(), returnedDTO.getTransactions());
        assertEquals("/api/tags/" + savedTag.getId() , returnedDTO.getPath());

        verify(tagRepository, times(1)).findByName(passedDTO.getName());
    }

    @Test
    void createNewTag_NameAlreadyExists() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO(NAME, COLOR);
        passedDTO.getTransactions().add(TRANSACTION);

        //Saved Entity
        Tag savedTag = createTagEntity();

        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.of(savedTag));

        assertThrows(ResourceAlreadyExistsException.class, () -> tagService.createNewTag(passedDTO));
    }

    @Test
    void updateTagById() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Updated Tag
        Tag updatedTag = new Tag(passedDTO.getName(), passedDTO.getColor());
        updatedTag.setId(original.getId());
        updatedTag.setTransactions(passedDTO.getTransactions());

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updatedTag)).thenReturn(updatedTag);


        TagDTO returnedDTO = tagService.updateTagById(original.getId(), passedDTO);


        assertEquals(original.getId(), returnedDTO.getId());
        assertEquals(passedDTO.getName(), returnedDTO.getName());
        assertEquals(passedDTO.getColor(), returnedDTO.getColor());
        assertEquals(passedDTO.getTransactions(), returnedDTO.getTransactions());
        assertEquals("/api/tags/" + original.getId() , returnedDTO.getPath());

        verify(tagRepository, times(1)).findByName(passedDTO.getName());
    }

    @Test
    void updateTagById_NotFound() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tagService.updateTagById(original.getId(), passedDTO));
    }

    @Test
    void updateTagById_NameAlreadyExists() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Saved Entity already using passedDTO name
        Tag anotherSavedEntity = createTagEntity();
        anotherSavedEntity.setName("Test Update");


        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> tagService.updateTagById(original.getId(), passedDTO));
    }

    @Test
    void patchTagById() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO("Test Patch", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Tag that will be updated and saved
        Tag updatedTag = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(updatedTag));
        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updatedTag)).thenReturn(updatedTag);


        TagDTO returnedDTO = tagService.patchTagById(original.getId(), passedDTO);


        assertEquals(original.getId(), returnedDTO.getId());
        assertEquals(passedDTO.getName(), returnedDTO.getName());
        assertEquals(passedDTO.getColor(), returnedDTO.getColor());
        assertEquals(original.getTransactions(), returnedDTO.getTransactions());
        assertEquals("/api/tags/" + original.getId() , returnedDTO.getPath());

        verify(tagRepository, times(1)).findByName(passedDTO.getName());
    }

    @Test
    void patchTagById_UpdateOnlyName() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO();
        passedDTO.setName("Test Patch");

        //Saved Entity
        Tag original = createTagEntity();

        //Tag that will be updated and saved
        Tag updatedTag = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(updatedTag));
        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(updatedTag)).thenReturn(updatedTag);


        TagDTO returnedDTO = tagService.patchTagById(original.getId(), passedDTO);


        assertEquals(original.getId(), returnedDTO.getId());
        assertEquals(passedDTO.getName(), returnedDTO.getName());
        assertEquals(original.getColor(), returnedDTO.getColor());
        assertEquals(original.getTransactions(), returnedDTO.getTransactions());
        assertEquals("/api/tags/" + original.getId() , returnedDTO.getPath());

        verify(tagRepository, times(1)).findByName(passedDTO.getName());
    }

    @Test
    void patchTagById_UpdateOnlyColor() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO();
        passedDTO.setColor(Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        //Tag that will be updated and saved
        Tag updatedTag = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(updatedTag));
        when(tagRepository.save(updatedTag)).thenReturn(updatedTag);


        TagDTO returnedDTO = tagService.patchTagById(original.getId(), passedDTO);


        assertEquals(original.getId(), returnedDTO.getId());
        assertEquals(original.getName(), returnedDTO.getName());
        assertEquals(passedDTO.getColor(), returnedDTO.getColor());
        assertEquals(original.getTransactions(), returnedDTO.getTransactions());
        assertEquals("/api/tags/" + original.getId() , returnedDTO.getPath());

        verify(tagRepository, never()).findByName(passedDTO.getName());
    }

    @Test
    void patchTagById_NotFound() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO("Test Patch", Color.GREEN);

        //Saved Entity
        Tag original = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class,
                () -> tagService.patchTagById(original.getId(), passedDTO));
    }

    @Test
    void patchTagById_NameAlreadyExists() {

        //DTO passed to function
        TagDTO passedDTO = new TagDTO();
        passedDTO.setName("Test Patch");

        //Saved Entity
        Tag original = createTagEntity();

        //Another Saved Entity with the passedDTO name
        Tag anotherSavedEntity = createTagEntity();
        anotherSavedEntity.setName("Test Patch");

        //Tag that will be updated and saved
        Tag updatedTag = createTagEntity();

        when(tagRepository.findById(original.getId())).thenReturn(Optional.of(updatedTag));
        when(tagRepository.findByName(passedDTO.getName())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> tagService.patchTagById(original.getId(), passedDTO));
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