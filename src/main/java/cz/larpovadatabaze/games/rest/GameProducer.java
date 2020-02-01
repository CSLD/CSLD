package cz.larpovadatabaze.games.rest;

import com.google.gson.Gson;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.models.RestGameDto;
import cz.larpovadatabaze.games.services.Games;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;

import java.io.IOException;

public class GameProducer extends AbstractResource {
    private Games games;

    public GameProducer(Games games) {
        this.games = games;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        PageParameters params = attributes.getParameters();
        int id = params.get("id").toInt();
        Game game = games.getById(id);

        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("application/json");
        resourceResponse.setTextEncoding("utf-8");

        if (game == null) {
            resourceResponse.setWriteCallback(new WriteCallback() {
                @Override
                public void writeData(Attributes attributes) throws IOException {
                    attributes.getResponse().write(
                            "{\"message\": \"There is no game with id: " + id + "\"}");
                }
            });
        } else {
            String gameDetailInJson = new Gson().toJson(new RestGameDto(
                    game.getName(), game.getDescription(), game.getYear(), game.getWeb(), game.getAverageRating(),
                    game.getTotalRating()
            ));
            resourceResponse.setWriteCallback(new WriteCallback() {
                @Override
                public void writeData(Attributes attributes) throws IOException {
                    attributes.getResponse().write(gameDetailInJson);
                }
            });
        }

        return resourceResponse;
    }
}
