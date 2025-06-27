package hr.algebra.xiaohongshuiis.config;

import hr.algebra.xiaohongshuiis.model.User;
import hr.algebra.xiaohongshuiis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            List<User> mockUsers = createMockUsers();
            userRepository.saveAll(mockUsers);
        }
    }

    private List<User> createMockUsers() {
        List<User> mockUsers = new ArrayList<>();

        mockUsers.add(new User("575923cd5e87e76293ee026e", "momo", "269564620"));
        mockUsers.get(0).setFollowersCount(14000);
        mockUsers.get(0).setNotesCount(18);
        mockUsers.get(0).setVerified(false);

        mockUsers.add(new User("5be53c600a30f70001de32ec", "momo", "530708219"));
        mockUsers.get(1).setFollowersCount(15000);
        mockUsers.get(1).setNotesCount(0);
        mockUsers.get(1).setVerified(false);

        mockUsers.add(new User("60979a440000000001008b55", "momo", "1146633016"));
        mockUsers.get(2).setFollowersCount(15000);
        mockUsers.get(2).setNotesCount(205);
        mockUsers.get(2).setVerified(false);

        mockUsers.add(new User("59059f4b82ec39332086b375", "momo", "941754671"));
        mockUsers.get(3).setFollowersCount(205000);
        mockUsers.get(3).setNotesCount(3);
        mockUsers.get(3).setVerified(false);

        mockUsers.add(new User("54856d7fd6e4a91280af0008", "momo", "August_ST5114"));
        mockUsers.get(4).setFollowersCount(15000);
        mockUsers.get(4).setNotesCount(281);
        mockUsers.get(4).setVerified(false);

        mockUsers.add(new User("56c5a93cb8c8b42199de52c4", "momo", "106263800"));
        mockUsers.get(5).setFollowersCount(205000);
        mockUsers.get(5).setNotesCount(71);
        mockUsers.get(5).setVerified(false);

        mockUsers.add(new User("621e36b1000000001000c5a6", "momo", "5230948104"));
        mockUsers.get(6).setFollowersCount(13000);
        mockUsers.get(6).setNotesCount(0);
        mockUsers.get(6).setVerified(false);

        mockUsers.add(new User("54cef19d2e1d935ac7b3d080", "momo", "lifeisrelax"));
        mockUsers.get(7).setFollowersCount(19000);
        mockUsers.get(7).setNotesCount(125);
        mockUsers.get(7).setVerified(false);

        mockUsers.add(new User("642558870000000029014ffa", "momo", "9693881157dd"));
        mockUsers.get(8).setFollowersCount(208000);
        mockUsers.get(8).setNotesCount(96);
        mockUsers.get(8).setVerified(false);

        mockUsers.add(new User("64d27871000000002b00aba8", "momo", "2016748091"));
        mockUsers.get(9).setFollowersCount(15000);
        mockUsers.get(9).setNotesCount(30);
        mockUsers.get(9).setVerified(false);

        mockUsers.add(new User("test001", "ana", "ana_test_123"));
        mockUsers.get(10).setFollowersCount(5000);
        mockUsers.get(10).setNotesCount(45);
        mockUsers.get(10).setVerified(true);

        mockUsers.add(new User("test002", "marko", "marko_dev_456"));
        mockUsers.get(11).setFollowersCount(8500);
        mockUsers.get(11).setNotesCount(67);
        mockUsers.get(11).setVerified(false);

        return mockUsers;
    }
}