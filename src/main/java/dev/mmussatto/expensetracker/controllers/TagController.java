/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.mappers.TransactionMapper;
import dev.mmussatto.expensetracker.api.model.ListDTO;
import dev.mmussatto.expensetracker.api.model.TagDTO;
import dev.mmussatto.expensetracker.api.model.TransactionDTO;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.services.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TagDTO> getAllTags () {
        return new ListDTO<>(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO getTagById (@PathVariable final Integer id) {
        return tagService.getTagById(id);
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO getTagByName (@PathVariable final String name) {
        return tagService.getTagByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDTO createNewTag (@RequestBody TagDTO tagDTO) {
        return tagService.createNewTag(tagDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO updateTagById (@PathVariable final Integer id, @RequestBody TagDTO tagDTO) {
        return tagService.updateTagById(id, tagDTO);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO patchTagById (@PathVariable final Integer id, @RequestBody TagDTO tagDTO) {
        return tagService.patchTagById(id, tagDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTagById (@PathVariable final Integer id) {
        tagService.deleteTagById(id);
    }

    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public Set<TransactionDTO> getPaymentMethodTransactionsById (@PathVariable final Integer id) {
        Set<Transaction> transactions = tagService.getTagTransactionsById(id);

        //Convert to dto
        return transactions.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    transactionDTO.setPath("/api/transactions/" + transactionDTO.getId());
                    return transactionDTO;
                }).collect(Collectors.toSet());
    }
}
