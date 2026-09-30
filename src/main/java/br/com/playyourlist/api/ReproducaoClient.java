package br.com.playyourlist.api;

import br.com.playyourlist.reproducao.Reproducao;
import br.com.playyourlist.reproducao.ReproducaoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "reproducoes", url = "${app.base-url}")
public interface ReproducaoClient {
    @PostMapping("/statistic")
    Reproducao registrar(@RequestBody ReproducaoRequest dados);
}
