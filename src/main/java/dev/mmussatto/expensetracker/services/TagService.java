/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.model.TagDTO;
import dev.mmussatto.expensetracker.domain.Transaction;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<TagDTO> getAllTags ();

    TagDTO getTagById (Integer id);

    TagDTO getTagByName (String name);

    TagDTO createNewTag (TagDTO tagDTO);

    TagDTO updateTagById (Integer id, TagDTO tagDTO);

    TagDTO patchTagById (Integer id, TagDTO tagDTO);

    void deleteTagById (Integer id);

    Set<Transaction> getTagTransactionsById(Integer id);
}
