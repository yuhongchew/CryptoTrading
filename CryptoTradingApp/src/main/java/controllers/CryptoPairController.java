package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repositories.CryptoPairRepository;

@RestController
@RequestMapping("/api/crypto-pair")
public class CryptoPairController {

    @Autowired
    private CryptoPairRepository cryptoPairRepository;

}
