package ensisa.birds;

import ensisa.birds.model.BirdRepository;

public class MainController {
    private BirdRepository repository;

    public MainController() {
        repository = new BirdRepository();
        repository.load();
    }
}
