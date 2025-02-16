import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles the backend operations for a social media platform, including:
 * user management, post creation, and interaction handling.
 */
public class BackEnd {

    private FileWriter writer;                      // Writer for logging actions
    private MyHashMap<String, User> allUsers;       // Map of all users by their unique IDs
    private MyHashMap<String, Post> allPosts;       // Map of all posts by their unique IDs

    /**
     * Constructs a backend system with the specified writer for logging.
     *
     * @param writer the FileWriter object for logging operations
     */
    public BackEnd(FileWriter writer) {
        this.writer = writer;
        allUsers = new MyHashMap<String, User>();
        allPosts = new MyHashMap<String, Post>();
    }

    /**
     * Creates a new user with the specified ID.
     *
     * @param userID the unique ID of the user
     * @throws IOException if an error occurs during writing to the log
     */
    public void createUser(String userID) throws IOException {
        if (allUsers.get(userID) == null) {
            allUsers.add(userID, new User(userID));
            writer.write("Created user with Id " + userID + ".\n");
            return;
        }

        writer.write("Some error occurred in create_user.\n");
    }

    /**
     * Allows a user to follow another user.
     *
     * @param userID1 the ID of the user who is following
     * @param userID2 the ID of the user to be followed
     * @throws IOException if an error occurs during writing to the log
     */
    public void followUser(String userID1, String userID2) throws IOException {
        User user1 = allUsers.get(userID1);
        User user2 = allUsers.get(userID2);

        if (user1 == null || user2 == null || user1 == user2) {
            writer.write("Some error occurred in follow_user.\n");
            return;
        }

        boolean done = user1.follow(user2);
        if (done) {
            writer.write(userID1 + " followed " + userID2 + ".\n");
        } else {
            writer.write("Some error occurred in follow_user.\n");
        }
    }

    /**
     * Allows a user to unfollow another user.
     *
     * @param userID1 the ID of the user who is unfollowing
     * @param userID2 the ID of the user to be unfollowed
     * @throws IOException if an error occurs during writing to the log
     */
    public void unfollowUser(String userID1, String userID2) throws IOException {
        User user1 = allUsers.get(userID1);
        User user2 = allUsers.get(userID2);

        if (user1 == null || user2 == null || user1 == user2) {
            writer.write("Some error occurred in unfollow_user.\n");
            return;
        }

        boolean done = user1.unfollow(user2);
        if (done) {
            writer.write(userID1 + " unfollowed " + userID2 + ".\n");
        } else {
            writer.write("Some error occurred in unfollow_user.\n");
        }
    }

    /**
     * Creates a new post for a user.
     *
     * @param userID  the ID of the user creating the post
     * @param postID  the unique ID of the post
     * @param content the content of the post
     * @throws IOException if an error occurs during writing to the log
     */
    public void createPost(String userID, String postID, String content) throws IOException {
        User user = allUsers.get(userID);

        if (user == null || allPosts.get(postID) != null) {
            writer.write("Some error occurred in create_post.\n");
            return;
        }

        Post post = new Post(postID, content, user);
        user.createPost(post);
        allPosts.add(postID, post);
        writer.write(userID + " created a post with Id " + postID + ".\n");
    }

    /**
     * Marks a post as seen by a user.
     *
     * @param userID the ID of the user viewing the post
     * @param postID the ID of the post being viewed
     * @throws IOException if an error occurs during writing to the log
     */
    public void seePost(String userID, String postID) throws IOException {
        User user = allUsers.get(userID);
        Post post = allPosts.get(postID);

        if (post == null || user == null) {
            writer.write("Some error occurred in see_post.\n");
            return;
        }

        post.markSeen(user);
        writer.write(userID + " saw " + postID + ".\n");
    }

    /**
     * Marks all posts from a user as seen by another user.
     *
     * @param viewerID the ID of the user viewing the posts
     * @param viewedID the ID of the user whose posts are being viewed
     * @throws IOException if an error occurs during writing to the log
     */
    public void seeAllPostsFromUser(String viewerID, String viewedID) throws IOException {
        User viewer = allUsers.get(viewerID);
        User viewed = allUsers.get(viewedID);

        if (viewer == null || viewed == null) {
            writer.write("Some error occurred in see_all_posts_from_user.\n");
            return;
        }

        if (viewed.hasPost()) {
            ArrayList<Post> posts = viewed.getPosts();
            for (Post post : posts) {
                post.markSeen(viewer);
            }
        }

        writer.write(viewerID + " saw all posts of " + viewedID + ".\n");
    }

    /**
     * Toggles the like status of a post for a user.
     *
     * @param userID the ID of the user liking or unliking the post
     * @param postID the ID of the post
     * @throws IOException if an error occurs during writing to the log
     */
    public void pressLikeButton(String userID, String postID) throws IOException {
        User user = allUsers.get(userID);
        Post post = allPosts.get(postID);

        if (post == null || user == null) {
            writer.write("Some error occurred in toggle_like.\n");
            return;
        }

        boolean liked = post.toggleLike(user);
        if (liked) {
            writer.write(userID + " liked " + postID + ".\n");
        } else {
            writer.write(userID + " unliked " + postID + ".\n");
        }
    }

    /**
     * Generates a feed of posts for a user based on their followed users.
     *
     * @param userID the ID of the user requesting the feed
     * @param count  the number of posts to include in the feed
     * @throws IOException if an error occurs during writing to the log
     */
    public void generateFeed(String userID, int count) throws IOException {
        User user = allUsers.get(userID);

        if (user == null) {
            writer.write("Some error occurred in generate_feed.\n");
            return;
        }

        writer.write("Feed for " + userID + ":\n");
        MaxHeap<Post> feed = new MaxHeap<>();
        ArrayList<User> followedUsers = user.getFollowed().getValues();
        for (User followedUser : followedUsers) {
            ArrayList<Post> posts = followedUser.getPosts();
            for (Post post : posts) {
                if (!post.hasSeen(user)) {
                    feed.insert(post);
                }
            }
        }

        while (count > 0 && !feed.isEmpty()) {
            Post mostLikedPost = feed.deleteMax();
            writer.write("Post ID: " + mostLikedPost.getID() + ", Author: " + mostLikedPost.getAuthor().getID() + ", Likes: " + mostLikedPost.getLikes() + "\n");
            count--;
        }

        if (count > 0) {
            writer.write("No more posts available for " + userID + ".\n");
        }
    }

    /**
     * Allows a user to scroll through their feed and optionally like posts.
     *
     * @param userID      the ID of the user scrolling the feed
     * @param number      the number of posts to scroll through
     * @param likedPosts  an array indicating whether each post is liked or just viewed
     * @throws IOException if an error occurs during writing to the log
     */
    public void scrollThroughFeed(String userID, int number, String[] likedPosts) throws IOException {
        User user = allUsers.get(userID);

        if (user == null) {
            writer.write("Some error occurred in scroll_through_feed.\n");
            return;
        }

        MaxHeap<Post> feed = new MaxHeap<>();
        ArrayList<User> followedUsers = user.getFollowed().getValues();
        for (User followedUser : followedUsers) {
            ArrayList<Post> posts = followedUser.getPosts();
            for (Post post : posts) {
                if (!post.hasSeen(user)) {
                    feed.insert(post);
                }
            }
        }

        writer.write(userID + " is scrolling through feed:\n");
        int scrolled = 0;
        while (number > scrolled && !feed.isEmpty()) {
            Post mostLikedPost = feed.deleteMax();

            if (likedPosts[scrolled].equals("0")) {
                mostLikedPost.markSeen(user);
                writer.write(userID + " saw " + mostLikedPost.getID() + " while scrolling.\n");
            } else {
                mostLikedPost.toggleLike(user);
                writer.write(userID + " saw " + mostLikedPost.getID() + " while scrolling and clicked the like button.\n");
            }

            scrolled++;
        }

        if (number > scrolled) {
            writer.write("No more posts in feed.\n");
        }
    }

    /**
     * Sorts the posts of a user by their like count in descending order.
     *
     * @param userID the ID of the user whose posts are to be sorted
     * @throws IOException if an error occurs during writing to the log
     */
    public void sortPosts(String userID) throws IOException {
        User user = allUsers.get(userID);

        if (user == null) {
            writer.write("Some error occurred in sort_posts.\n");
            return;
        }

        if (!user.hasPost()) {
            writer.write("No posts from " + userID + ".\n");
            return;
        }

        writer.write("Sorting " + userID + "'s posts:\n");

        MaxHeap<Post> maxHeap = new MaxHeap<>();
        ArrayList<Post> posts = user.getPosts();
        for (Post post : posts) {
            maxHeap.insert(post);
        }

        int heapSize = maxHeap.size();
        for (int i = 0; i < heapSize; i++) {
            Post post = maxHeap.deleteMax();
            writer.write(post.getID() + ", Likes: " + post.getLikes() + "\n");
        }
    }
}
