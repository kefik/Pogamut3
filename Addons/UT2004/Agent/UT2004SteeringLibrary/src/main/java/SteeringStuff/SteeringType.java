package SteeringStuff;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * The enum of the types of the steerings. Each type has its name color and order. But the navigation layer doesn't use these values. (But for instance the SteeringGui does.)
 * @author Marki
 */
public enum SteeringType implements Serializable {
    //OBSTACLE_AVOIDANCE(1, "Obstacle Avoidance", new Color(255f/255f,186f/255,88f/255,0.5f)),    //světle oranžová  25-167-255
    OBSTACLE_AVOIDANCE(1, "Obstacle Avoidance", new Color(155f/255,101f/255,53f/255,0.5f)),             //tmavě hnědá 20-168-155
    PEOPLE_AVOIDANCE(2, "People Avoidance", new Color(230f/255f,128f/255,72f/255,0.5f)),         //tělová   15-175-230
    //TARGET_APPROACHING(3, "Target Approaching", new Color(206f/255f,255f/255,87f/255,0.5f)),    //brčálově světle zelená   55-168-255
    //TARGET_APPROACHING(3, "Target Approaching", new Color(172f/255f,255f/255,23f/255,0.5f)),    //světle zelená   55-168-255
    TARGET_APPROACHING(3, "Target Approaching", new Color(144f/255f,211f/255,21f/255,0.5f)),    //brčálově světle zelená   55-168-255
    PATH_FOLLOWING(4, "Path Following", new Color(65f/255f,155f/255,53f/255,0.3f)),            //tmavě zelená 80-168-155
    //WALL_FOLLOWING(5, "Wall Following", new Color(155f/255,101f/255,53f/255,0.5f)),             //tmavě hnědá 20-168-155
    WALL_FOLLOWING(5, "Wall Following", new Color(255f/255f,186f/255,88f/255,0.5f)),            //světle oranžová  25-167-255
    LEADER_FOLLOWING(6, "Leader Following", new Color(60f/255f,175f/255,100f/255,0.5f)),       //tmavší tyrkysová 100-167-175
    WALK_ALONG(7, "Walk Along", new Color(255f/255f,145f/255,86f/255,0.5f)),                    //tmavě oranžová 15-168-255
    TRIANGLE(8, "Triangle", new Color(0,0,0,0.5f)),                    //cerna
    STICK_TO_PATH(9, "Stick To Path", new Color(0,0,0,0.5f));

    private int order;
    private String name;
    private Color color;

    private SteeringType(int order, String name, Color color) {
        this.order = order;
        this.name = name;
        this.color = color;
    }
    
    public static LinkedList<SteeringType> getCollection() {
        LinkedList<SteeringType> result = new LinkedList<SteeringType>();

        for (SteeringType type : SteeringType.values()) {
            result.add(type);
        }
        return result;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }
}

/*
    OBSTACLE_AVOIDANCE(1, "Obstacle Avoidance", new Color(238f/255,137f/255,26f/255,0.5f)),
    PEOPLE_AVOIDANCE(2, "People Avoidance", new Color(255f/255,174f/255,156f/255,0.5f)),
    TARGET_APPROACHING(3, "Target Approaching", new Color(179f/255,255f/255,66f/255,0.5f)),
    PATH_FOLLOWING(4, "Path Following", new Color(116f/255,255f/255,66f/255,0.5f)),
    WALL_FOLLOWING(5, "Wall Following", new Color(255f/255,166f/255,66f/255,0.5f)),
    LEADER_FOLLOWING(6, "Leader Following", new Color(66f/255,255f/255,157f/255,0.5f)),
    WALK_ALONG(7, "Walk Along", new Color(66f/255,255f/255,214f/255,0.5f));


    OBSTACLE_AVOIDANCE(1, "Obstacle Avoidance", new Color(25f/255f,0.85f,1.0f,0.5f)),    //světle oranžová  25-167-255
    PEOPLE_AVOIDANCE(2, "People Avoidance", new Color(15f/255f,0.7f,0.9f,0.5f)),         //tělová   15-175-230
    TARGET_APPROACHING(3, "Target Approaching", new Color(55f/255f,0.85f,1.0f,0.5f)),    //brčálově světle zelená   55-168-255
    PATH_FOLLOWING(4, "Path Following", new Color(80f/255f,0.85f,0.6f,0.3f)),            //tmavě zelená 80-168-155
    WALL_FOLLOWING(5, "Wall Following", new Color(20f/255f,0.85f,0.6f,0.5f)),             //tmavě hnědá 20-168-155
    LEADER_FOLLOWING(6, "Leader Following", new Color(100f/255f,0.85f,0.7f,0.5f)),       //tmavší tyrkysová 100-167-175
    WALK_ALONG(7, "Walk Along", new Color(15f/255f,0.85f,1.0f,0.5f));                    //tmavě oranžová 20-167-155*/