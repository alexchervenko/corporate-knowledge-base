package ru.chervenko.EnsetKB.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.chervenko.EnsetKB.dto.ProblemDTO;
import ru.chervenko.EnsetKB.model.Problem;
import ru.chervenko.EnsetKB.service.ProblemService;

@Controller
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProblemController(ProblemService problemService,
                             ModelMapper modelMapper) {
        this.problemService = problemService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public String index(@RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "5") int size,
                        Model model) {
        model.addAttribute("problems", problemService.findAllByPage(PageRequest.of(page, size)));
        return "problems/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") String id,
                       Model model) {
        if (problemService.findById(id).isPresent()) {
            model.addAttribute("problem", problemService.findById(id).get());
            return "problems/show";
        } else {
            return "errors/error-404";
        }
    }

    @GetMapping("/new")
    public String newProblem(@ModelAttribute("problem") @Valid ProblemDTO problemDTO) {
        return "problems/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("problem") @Valid ProblemDTO problemDTO,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "problems/new";
        problemService.save(convertToProblem(problemDTO));
        return "redirect:/problems";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") String id,
                       Model model) {
        if (problemService.findById(id).isPresent()) {
            model.addAttribute("problem", problemService.findById(id).get());
            return "problems/edit";
        } else {
            return "errors/error-404";
        }
    }

    @PatchMapping("/{id}/edit")
    public String update(@ModelAttribute("problem") @Valid ProblemDTO problemDTO,
                         BindingResult bindingResult,
                         @PathVariable("id") String id) {
        if (bindingResult.hasErrors()) return "problems/edit";
        problemService.update(id, convertToProblem(problemDTO));
        return "redirect:/problems/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") String id) {
        problemService.delete(id);
        return "redirect:/problems";
    }

    private Problem convertToProblem(ProblemDTO problemDTO) {
        return modelMapper.map(problemDTO, Problem.class);
    }


}
