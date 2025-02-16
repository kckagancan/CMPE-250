/**
 * A class that represents a social media post with unique ID, content, author, and likes.
 */
public class Post implements Comparable<Post> {

    private final String ID;                   // ID of the post
    private final String content;             // Content of the post
    private int likes;                        // Number of likes the post has received
    private MyHashMap<String, User> seenBy;   // HashMap of users who have seen the post
    private MyHashMap<String, User> likedBy;  // HashMap of users who have liked the post
    private final User author;                // Author of the post

    /**
     * Constructs a new post with the given ID, content, and author.
     *
     * @param ID      the unique identifier for the post
     * @param content the content of the post
     * @param author  the author of the post
     */
    public Post(String ID, String content, User author) {
        this.ID = ID;
        this.content = content;
        this.author = author;
        likes = 0;
        seenBy = new MyHashMap<String, User>();
        likedBy = new MyHashMap<String, User>();
    }

    /**
     * Checks whether a given user has seen this post.
     *
     * @param user the user to check
     * @return true if the user has seen the post, false otherwise
     */
    public boolean hasSeen(User user) {
        return (seenBy.get(user.getID()) != null);
    }

    /**
     * Marks the given user as having seen this post.
     *
     * @param user the user to mark as having seen the post
     */
    public void markSeen(User user) {
        if (!hasSeen(user)) {
            seenBy.add(user.getID(), user);
        }
    }

    /**
     * Toggles the like status of the given user on this post and marks the user as seen this post.
     *
     * @param user the user toggling their like
     * @return true if the post is now liked by the user, false if the like was removed
     */
    public boolean toggleLike(User user) {
        markSeen(user);

        if (likedBy.get(user.getID()) == null) {
            likedBy.add(user.getID(), user);
            likes++;
            return true;
        }

        likedBy.remove(user.getID());
        likes--;
        return false;
    }

    /**
     * Returns the unique identifier of this post.
     *
     * @return the unique ID of the post
     */
    public String getID() {
        return ID;
    }

    /**
     * Returns the number of likes this post has received.
     *
     * @return the number of likes
     */
    public int getLikes() {
        return likes;
    }

    /**
     * Returns the author of this post.
     *
     * @return the author of the post
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Compares this post to another post based on the number of likes.
     * If the number of likes is the same, the comparison falls back to the ID.
     *
     * @param post the post to compare to
     * @return a negative integer, zero, or a positive integer as this post is less than, equal to, or greater than the specified post
     */
    @Override
    public int compareTo(Post post) {
        if (this.likes != post.getLikes())
            return this.likes - post.getLikes();

        return this.ID.compareTo(post.getID());
    }
}
