package com.gymsys.controller.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.CompetitionItem;
import com.gymsys.entity.competition.dto.competitionItem.AddCompetitionItemDTO;
import com.gymsys.entity.competition.dto.competitionItem.ListCompetitionItemDTO;
import com.gymsys.entity.competition.dto.competitionItem.UpdateCompetitionItemDTO;
import com.gymsys.service.competition.ICompetitionItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 赛事项目表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/competition-item")
@RequiredArgsConstructor
public class CompetitionItemController {

    private final ICompetitionItemService competitionItemService;

    @PostMapping("/add")
    public Result<?> addCompetitionItem(@RequestBody @Valid AddCompetitionItemDTO addCompetitionItemDTO) {
        competitionItemService.addCompetitionItem(addCompetitionItemDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCompetitionItem(@PathVariable Long id) {
        competitionItemService.deleteCompetitionItem(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @PutMapping("/update")
    public Result<?> updateCompetitionItem(@RequestBody @Valid UpdateCompetitionItemDTO updateCompetitionItemDTO) {
        competitionItemService.updateCompetitionItem(updateCompetitionItemDTO);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<IPage<CompetitionItem>> listCompetitionItem(@Valid ListCompetitionItemDTO listCompetitionItemDTO) {
        return Result.success(competitionItemService.listCompetitionItem(listCompetitionItemDTO));
    }

    @GetMapping("/get/{id}")
    public Result<CompetitionItem> getCompetitionItem(@PathVariable Long id) {
        return Result.success(competitionItemService.getById(id));
    }
    
    @GetMapping("/listAll")
    public Result<List<CompetitionItem>> listAllCompetitionItems() {
        return Result.success(competitionItemService.list());
    }
} 