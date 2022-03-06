package pro.gsilva.catalogo.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

import pro.gsilva.catalogo.model.Categoria;
import pro.gsilva.catalogo.model.Musica;
import pro.gsilva.catalogo.service.CatalogoService;
import pro.gsilva.catalogo.service.CategoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CatalogoController {
    
    @Autowired 
    private CatalogoService catalogoService;

    @Autowired 
    private CategoriaService categoriaService;

    @RequestMapping(value="/musicas", method=RequestMethod.GET)
    public ModelAndView getMusicas() {
        ModelAndView mv = new ModelAndView("musicas");
        List<Musica> musicas = catalogoService.findAll();
        mv.addObject("musicas", musicas);
        mv.addObject("categorias", this.categoriaService.findAll(PageRequest.of(0, 20)).getContent());
        return mv;
    }

    @RequestMapping(value="/musicas/{id}", method=RequestMethod.GET)
    public ModelAndView getMusicaDetalhes(@PathVariable("id") long id) {
        ModelAndView mv = new ModelAndView("musicaDetalhes");
        Musica musica = catalogoService.findById(id);
        mv.addObject("musica", musica);
        return mv;
    }

    @RequestMapping(value = "/musicas/edit/{id}", method = RequestMethod.GET)
    public ModelAndView getFormEdit(@PathVariable("id") long id) {
        ModelAndView mv = new ModelAndView("musicaForm");
        Musica musica = catalogoService.findById(id);
        mv.addObject("musica", musica);
        mv.addObject("categorias", this.categoriaService.findAll(PageRequest.of(0, 20)).getContent());
        return mv;
    }

    @RequestMapping(value="/addMusica", method=RequestMethod.GET)
    public ModelAndView getMusicaForm(Musica musica) {
        ModelAndView musicaForm = new ModelAndView("musicaForm");
        Page<Categoria> categorias = this.categoriaService.findAll(PageRequest.of(0, 20));
        musicaForm.addObject("musica", musica);
        musicaForm.addObject("categorias", categorias.getContent());
        return musicaForm;
    }
    
    @RequestMapping(value="/addMusica", method=RequestMethod.POST)
    public ModelAndView salvarMusica(@Valid @ModelAttribute("musica") Musica musica, 
           BindingResult result, Model model) {
        if (result.hasErrors()) {
            ModelAndView musicaForm = new ModelAndView("musicaForm");
            musicaForm.addObject("mensagem", "Verifique os errors do formulário");
            musicaForm.addObject("categorias", this.categoriaService.findAll(PageRequest.of(0, 20)).getContent());
            return musicaForm;
        }
        musica.setData(LocalDate.now());
        catalogoService.save(musica);
        return new ModelAndView("redirect:/musicas");
    }

    @GetMapping("/musicas/pesquisar")
    public ModelAndView pesquisar(@RequestParam("titulo") String titulo, 
        @RequestParam(name = "categoria", defaultValue = "0") long categoriaId) {
        ModelAndView mv = new ModelAndView("musicas");
        List<Musica> musicas;
        if (categoriaId != 0) {
            musicas = catalogoService.findByTituloAndCategoriaId(titulo, categoriaId);
        } else {
            musicas = catalogoService.findByTitulo(titulo);
        }
        mv.addObject("musicas", musicas);
        mv.addObject("categorias", this.categoriaService.findAll(PageRequest.of(0, 20)).getContent());
        return mv;
    }
    
    @RequestMapping(value="/delMusica/{id}", method=RequestMethod.GET)
    public String delMusica(@PathVariable("id") long id) {
        catalogoService.excluir(id);
        return "redirect:/musicas";
    }
        

}
