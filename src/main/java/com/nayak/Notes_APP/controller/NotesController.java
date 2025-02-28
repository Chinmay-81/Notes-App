package com.nayak.Notes_APP.controller;

import com.nayak.Notes_APP.entity.Notes;
import com.nayak.Notes_APP.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    NotesService notesService;

    @PostMapping("/saveNote")
    public Notes saveEachNote(@RequestBody Notes notes){
        return notesService.saveNotes(notes);
    }

    @GetMapping("/{language}")
    public ResponseEntity<List<Notes>> getNotesByLangControl(@PathVariable String language){
        List<Notes> notes = notesService.getNotesByLang(language);
        return ResponseEntity.ok(notes);
        
    }

}
