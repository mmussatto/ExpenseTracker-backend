/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import dev.mmussatto.expensetracker.entities.helpers.ListDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    public TagController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TagDTO> getAllTags () {
        return new ListDTO<>(tagService.getAllTags()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO getTagById (@PathVariable final Integer id) {
        return convertToDTO(tagService.getTagById(id));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO getTagByName (@PathVariable final String name) {
        return convertToDTO(tagService.getTagByName(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(TagDTO.allFieldsValidation.class)
    public TagDTO createNewTag (@Valid @RequestBody TagDTO tagDTO) {
        Tag entity = convertToEntity(tagDTO);
        return convertToDTO(tagService.createNewTag(entity));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(TagDTO.allFieldsValidation.class)
    public TagDTO updateTagById (@PathVariable final Integer id, @Valid @RequestBody TagDTO tagDTO) {
        Tag entity = convertToEntity(tagDTO);
        return convertToDTO(tagService.updateTagById(id, entity));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(TagDTO.onlyIdValidation.class)
    public TagDTO patchTagById (@PathVariable final Integer id, @Valid @RequestBody TagDTO tagDTO) {
        Tag entity = convertToEntity(tagDTO);
        return convertToDTO(tagService.patchTagById(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTagById (@PathVariable final Integer id) {
        tagService.deleteTagById(id);
    }

    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TransactionDTO> getPaymentMethodTransactionsById (@PathVariable final Integer id) {
        Set<Transaction> transactions = tagService.getTagTransactionsById(id);

        //Convert to dto
        return new ListDTO<>(transactions.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    transactionDTO.setPath("/api/transactions/" + transactionDTO.getId());
                    return transactionDTO;
                }).collect(Collectors.toList()));
    }

    private TagDTO convertToDTO (Tag entity) {
        TagDTO tagDTO = tagMapper.convertToDTO(entity);
        tagDTO.setPath("/api/tags/" + tagDTO.getId());
        return tagDTO;
    }

    private Tag convertToEntity (TagDTO dto) {
        return tagMapper.convertToEntity(dto);
    }
}
