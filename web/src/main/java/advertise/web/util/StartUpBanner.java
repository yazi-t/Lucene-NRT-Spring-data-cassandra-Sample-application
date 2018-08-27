package advertise.web.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StartUpBanner implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        print();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private static void print() {
        try {
        System.out.println("__________________________________________________________________________________");
        System.out.println("   _____                       _                    _  __          __  _      \n" +
                "  / ____|                     | |          /\\      | | \\ \\        / / | |     \n" +
                " | (___   __ _ _ __ ___  _ __ | | ___     /  \\   __| |  \\ \\  /\\  / /__| |__   \n" +
                "  \\___ \\ / _` | '_ ` _ \\| '_ \\| |/ _ \\   / /\\ \\ / _` |   \\ \\/  \\/ / _ \\ '_ \\  \n" +
                "  ____) | (_| | | | | | | |_) | |  __/  / ____ \\ (_| |    \\  /\\  /  __/ |_) | \n" +
                " |_____/ \\__,_|_| |_| |_| .__/|_|\\___| /_/    \\_\\__,_|     \\/  \\/ \\___|_.__/  \n" +
                "                        | |                                                   \n" +
                "                        |_|                                                                   Designed by Yasitha Thilakaratne");
        System.out.println("__________________________________________________________________________________");
        System.out.println("    _                 _           ___                     _          \n" +
                "   /_\\  _ __  __ _ __| |_  ___   / __|__ _ ______ __ _ __| |_ _ __ _ \n" +
                "  / _ \\| '_ \\/ _` / _| ' \\/ -_) | (__/ _` (_-<_-</ _` / _` | '_/ _` |\n" +
                " /_/ \\_\\ .__/\\__,_\\__|_||_\\___|  \\___\\__,_/__/__/\\__,_\\__,_|_| \\__,_|\n" +
                "       |_|                                                           ");
        System.out.println("__________________________________________________________________________________");
        System.out.println("    _                 _          _                          _  _ ___ _____ \n" +
                "   /_\\  _ __  __ _ __| |_  ___  | |  _  _ __ ___ _ _  ___  | \\| | _ \\_   _|\n" +
                "  / _ \\| '_ \\/ _` / _| ' \\/ -_) | |_| || / _/ -_) ' \\/ -_) | .` |   / | |  \n" +
                " /_/ \\_\\ .__/\\__,_\\__|_||_\\___| |____\\_,_\\__\\___|_||_\\___| |_|\\_|_|_\\ |_|  \n" +
                "       |_|                                                                 ");
        System.out.println("__________________________________________________________________________________");
        System.out.println("  ___             _        _    \n" +
                " | _ \\___ __ _ __| |_   _ | |___\n" +
                " |   / -_) _` / _|  _| | || (_-<\n" +
                " |_|_\\___\\__,_\\__|\\__|  \\__//__/\n" +
                "                                ");
        System.out.println("__________________________________________________________________________________");
        Thread.sleep(700);

        } catch (Exception e) {
            //
        }
    }

    public static void main(String [] args) {
        print();
    }
}
