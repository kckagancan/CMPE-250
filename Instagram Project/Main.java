import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Instagram-Like App
 *
 * @author Kagan Can, Student ID: 2022400240
 * @since Date: 15.11.2024
 */
public class Main {
    public static void main(String[] args) throws IOException {

        // Initialize a file handle and a scanner for the input, a file writer for the output.
        File fh = new File(args[0]);
        Scanner scanner = new Scanner(fh);
        FileWriter writer = new FileWriter(args[1]);
        BackEnd backEnd = new BackEnd(writer);

        // Read the input file.
        while (scanner.hasNextLine()) {
            try {
                String operation = scanner.next();

                // Act accordingly to the operation.
                switch (operation) {
                    case "create_user": {
                        String userID = scanner.next();
                        backEnd.createUser(userID);
                        break;
                    }
                    case "follow_user": {
                        String userID1 = scanner.next();
                        String userID2 = scanner.next();
                        backEnd.followUser(userID1, userID2);
                        break;
                    }
                    case "unfollow_user": {
                        String userID1 = scanner.next();
                        String userID2 = scanner.next();
                        backEnd.unfollowUser(userID1, userID2);
                        break;
                    }
                    case "create_post": {
                        String userID = scanner.next();
                        String postID = scanner.next();
                        String content = scanner.next();
                        backEnd.createPost(userID, postID, content);
                        break;
                    }
                    case "see_post": {
                        String userID = scanner.next();
                        String postID = scanner.next();
                        backEnd.seePost(userID, postID);
                        break;
                    }
                    case "see_all_posts_from_user": {
                        String viewerID = scanner.next();
                        String viewedID = scanner.next();
                        backEnd.seeAllPostsFromUser(viewerID, viewedID);
                        break;
                    }
                    case "toggle_like": {
                        String userID = scanner.next();
                        String postID = scanner.next();
                        backEnd.pressLikeButton(userID, postID);
                        break;
                    }
                    case "generate_feed": {
                        String userID = scanner.next();
                        int number = scanner.nextInt();
                        backEnd.generateFeed(userID, number);
                        break;
                    }
                    case "scroll_through_feed": {
                        String userID = scanner.next();
                        int number = scanner.nextInt();
                        String[] likedPosts = scanner.nextLine().stripLeading().split(" ");
                        backEnd.scrollThroughFeed(userID, number, likedPosts);
                        break;
                    }
                    case "sort_posts": {
                        String userID = scanner.next();
                        backEnd.sortPosts(userID);
                        break;
                    }
                }
            }

            catch (NoSuchElementException ignored) {
            }
        }

        writer.close();
    }
}