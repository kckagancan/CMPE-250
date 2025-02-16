import java.util.ArrayList;

/**
 * Represents a user in a social media system with a unique ID and a follower count.
 * It also manages the posts of a user and the people user follows.
 */
public class User implements Comparable<User> {

    private final String ID;                       // Unique identifier for the user
    private int followerCount;                     // Number of followers the user has
    private MyHashMap<String, User> followed;      // Users followed by this user
    private ArrayList<Post> posts;                 // Posts created by this user

    /**
     * Constructs a new user with the specified unique ID.
     *
     * @param ID the unique identifier for the user
     */
    public User(String ID) {
        this.ID = ID;
        followerCount = 0;
        followed = new MyHashMap<String, User>();
        posts = new ArrayList<Post>();
    }

    /**
     * Follows another user.
     *
     * @param user the user to follow
     * @return true if the user was successfully followed, false if already following
     */
    public boolean follow(User user) {
        if (!isFollowing(user)) {
            followed.add(user.getID(), user);
            user.addFollower();
            return true;
        }

        return false;
    }

    /**
     * Unfollows another user.
     *
     * @param user the user to unfollow
     * @return true if the user was successfully unfollowed, false if not currently following
     */
    public boolean unfollow(User user) {
        if (isFollowing(user)) {
            followed.remove(user.getID());
            user.removeFollower();
            return true;
        }

        return false;
    }

    /**
     * Increments the follower count for this user.
     */
    public void addFollower() {
        followerCount++;
    }

    /**
     * Decrements the follower count for this user.
     */
    public void removeFollower() {
        followerCount--;
    }

    /**
     * Checks if this user is following another user.
     *
     * @param user the user to check
     * @return true if this user is following the specified user, false otherwise
     */
    boolean isFollowing(User user) {
        return followed.get(user.getID()) != null;
    }

    /**
     * Creates a post and associates it with this user.
     *
     * @param post the post to be created
     */
    public void createPost(Post post) {
        posts.add(post);
    }

    /**
     * Checks if this user has created any posts.
     *
     * @return true if the user has at least one post, false otherwise
     */
    public boolean hasPost() {
        return !posts.isEmpty();
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the unique ID of the user
     */
    public String getID() {
        return ID;
    }

    /**
     * Returns the number of followers this user has.
     *
     * @return the follower count of the user
     */
    public int getFollowerCount() {
        return followerCount;
    }

    /**
     * Returns the users followed by this user.
     *
     * @return a map of followed users
     */
    public MyHashMap<String, User> getFollowed() {
        return followed;
    }

    /**
     * Returns a list of posts created by this user.
     *
     * @return an ArrayList of posts created by the user
     */
    public ArrayList<Post> getPosts() {
        return posts;
    }

    /**
     * Compares this user to another user based on follower count.
     * If the follower counts are equal, comparison falls back to their unique IDs.
     *
     * @param user the user to compare to
     * @return a negative integer, zero, or a positive integer as this user is less than, equal to, or greater than the specified user
     */
    @Override
    public int compareTo(User user) {
        if (this.followerCount != user.getFollowerCount())
            return this.followerCount - user.getFollowerCount();

        return this.ID.compareTo(user.getID());
    }
}
