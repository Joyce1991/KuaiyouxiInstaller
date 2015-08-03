package installer.libs.kuaiyuoxi.com.kyxinstaller.datas;

/**
 * Created by jalen-pc on 2015/7/31.
 */
public class User {
    private final String firstName;
    private final String lastName;
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
}
