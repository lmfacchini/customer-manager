package com.datum.client.manager.client.impl;

import com.datum.client.manager.client.ScoresApiClient;
import com.datum.client.manager.exception.ClientAccessException;
import com.datum.customer.adapter.output.score.generated.api.ScoresApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScoresApiClientImpl implements ScoresApiClient {

    private final ScoresApi scoresApi;


    @Override
    public int score(String cpf){
        var response = scoresApi.findScoreByCpfWithHttpInfo(cpf);
        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
            return response.getBody().getScore();
        }
        throw new ClientAccessException("ScoresApi - score", response.getStatusCode().value());
    }


}
