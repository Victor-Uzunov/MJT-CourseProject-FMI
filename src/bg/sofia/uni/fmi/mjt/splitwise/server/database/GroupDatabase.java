package bg.sofia.uni.fmi.mjt.splitwise.server.database;

import bg.sofia.uni.fmi.mjt.splitwise.interactions.Group;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.util.Set;

public class GroupDatabase extends Database<Group> {
    public static final String GROUPS_FILE_NAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\src\\bg\\"
            + "sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\groups.txt";

    public GroupDatabase() {
        super(GROUPS_FILE_NAME);
    }

    public GroupDatabase(String filepath) {
        super(filepath);
    }

    public void save() {
        super.save(GROUPS_FILE_NAME);
    }

    public void addGroup(String groupName, Set<User> users) {
        super.objects.put(groupName, new Group(groupName, users));
    }

    public boolean containsGroup(String groupName) {
        if (groupName == null) {
            throw new IllegalArgumentException("Group name cannot be null");
        }
        return super.objects.containsKey(groupName);
    }

    public Group getGroup(String groupName) {
        return super.objects.get(groupName);
    }
}
