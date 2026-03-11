package org.example.counter

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CountdownController {
    
    @GetMapping("/")
    fun index(): String {
        return "index"
    }
}

