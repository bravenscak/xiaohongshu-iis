package hr.algebra.xiaohongshuiis.service;

import hr.algebra.xiaohongshuiis.model.User;
import hr.algebra.xiaohongshuiis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XmlGeneratorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XiaohongshuService xiaohongshuService;

    public String generateUsersXml() {
        try {
            List<User> users = userRepository.findAll();

            if (users.isEmpty()) {
                System.out.println("No users in database, fetching from API for XML generation...");
                try {
                    users = xiaohongshuService.searchUsers("momo");
                    if (!users.isEmpty()) {
                        userRepository.saveAll(users);
                        System.out.println("Saved " + users.size() + " users from API to database");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to fetch users from API: " + e.getMessage());
                    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<users>\n</users>";
                }
            }

            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlBuilder.append("<users>\n");

            for (User user : users) {
                xmlBuilder.append("  <user>\n");
                xmlBuilder.append("    <id>").append(escapeXml(user.getId())).append("</id>\n");
                xmlBuilder.append("    <name>").append(escapeXml(user.getName())).append("</name>\n");
                xmlBuilder.append("    <redId>").append(escapeXml(user.getRedId())).append("</redId>\n");

                if (user.getFollowersCount() != null) {
                    xmlBuilder.append("    <followersCount>").append(user.getFollowersCount()).append("</followersCount>\n");
                }
                if (user.getNotesCount() != null) {
                    xmlBuilder.append("    <notesCount>").append(user.getNotesCount()).append("</notesCount>\n");
                }
                if (user.getVerified() != null) {
                    xmlBuilder.append("    <verified>").append(user.getVerified()).append("</verified>\n");
                }

                xmlBuilder.append("  </user>\n");
            }

            xmlBuilder.append("</users>");

            String result = xmlBuilder.toString();
            System.out.println("Generated XML with " + users.size() + " users");
            return result;

        } catch (Exception e) {
            System.err.println("Error generating XML: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generating XML: " + e.getMessage());
        }
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}