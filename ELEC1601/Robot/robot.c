#include "robot.h"

void setup_robot(struct Robot *robot){
    // /*
    //////////default maze///////////
    // robot->x = OVERALL_WINDOW_WIDTH/2-50;
    // robot->y = OVERALL_WINDOW_HEIGHT-50;
    // robot->true_x = OVERALL_WINDOW_WIDTH/2-50;
    // robot->true_y = OVERALL_WINDOW_HEIGHT-50;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 0;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;
    // robot->front_value = 0;
    // robot->right_value = 0;
    // robot->left_value = 0;
//////////////////////////////////////////////////////maze 1/////////////////////////////////////////////////////////
    // robot->x = 270;
    // robot->y = 460;
    // robot->true_x = 270;
    // robot->true_y = 460;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 0;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;

//////////////////////////////////////////////////////maze 2/////////////////////////////////////////////////////////
    // robot->x = 620;
    // robot->y = 380;
    // robot->true_x = 620;
    // robot->true_y = 380;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 270;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;
//////////////////////////////////////////////////////maze 3/////////////////////////////////////////////////////////

    // robot->x = 640-10-270;
    // robot->y = 460;
    // robot->true_x = 640-10-270;
    // robot->true_y = 460;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 0;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;

//////////////////////////////////////////////////////maze 4/////////////////////////////////////////////////////////
    
    // robot->x = 0;
    // robot->y = 380;
    // robot->true_x = 0;
    // robot->true_y = 380;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 90;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;

//////////////////////////////////////////////////////maze 5/////////////////////////////////////////////////////////

    // robot->x = 170;
    // robot->y = 460;
    // robot->true_x = 170;
    // robot->true_y = 460;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 0;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;

//////////////////////////////////////////////////////maze 6/////////////////////////////////////////////////////////
    // robot->x = 620;
    // robot->y = 40;
    // robot->true_x = 620;
    // robot->true_y = 40;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 270;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;

//////////////////////////////////////////////////////maze 7/////////////////////////////////////////////////////////
    robot->x = 640-10-170;
    robot->y = 460;
    robot->true_x = 640-10-170;
    robot->true_y = 460;
    robot->width = ROBOT_WIDTH;
    robot->height = ROBOT_HEIGHT;
    robot->direction = 0;
    robot->angle = 0;
    robot->currentSpeed = 0;
    robot->crashed = 0;
    robot->auto_mode = 0;

//////////////////////////////////////////////////////maze 8/////////////////////////////////////////////////////////

    // robot->x = 0;
    // robot->y = 40;
    // robot->true_x = 0;
    // robot->true_y = 40;
    // robot->width = ROBOT_WIDTH;
    // robot->height = ROBOT_HEIGHT;
    // robot->direction = 0;
    // robot->angle = 90;
    // robot->currentSpeed = 0;
    // robot->crashed = 0;
    // robot->auto_mode = 0;

    printf("Press arrow keys to move manually, or enter to move automatically\n\n");
}

int robot_off_screen(struct Robot * robot){
    if(robot->x < 0 || robot-> y < 0){
        return 0;
    }
    if(robot->x > OVERALL_WINDOW_WIDTH || robot->y > OVERALL_WINDOW_HEIGHT){
        return 0;
    }
    return 1;
}

int checkRobotHitWall(struct Robot * robot, struct Wall * wall) {

    int overlap = checkOverlap(robot->x,robot->width,robot->y,robot->height,
                wall->x,wall->width,wall->y, wall->height);

    return overlap;
}

int checkRobotHitWalls(struct Robot * robot, struct Wall_collection * head) {
   struct Wall_collection *ptr = head;
    int hit = 0;

    while(ptr != NULL) {
        hit = (hit || checkRobotHitWall(robot, &ptr->wall));
        ptr = ptr->next;
    }
    return hit;

}

int checkRobotReachedEnd(struct Robot * robot, int x, int y, int width, int height){

    int overlap = checkOverlap(robot->x,robot->width,robot->y,robot->height,
                x,width,y,height);

    return overlap;
}

void robotCrash(struct Robot * robot) {
    robot->currentSpeed = 0;
    if (!robot->crashed)
        printf("Ouchies!!!!!\n\nPress space to start again\n");
    robot->crashed = 1;
}

void robotSuccess(struct Robot * robot, int msec) {
    robot->currentSpeed = 0;
    if (!robot->crashed){
        printf("Success!!!!!\n\n");
        printf("Time taken %d seconds %d milliseconds \n", msec/1000, msec%1000);
        printf("Press space to start again\n");
    }
    robot->crashed = 1;
}

int checkRobotSensor(int x, int y, int sensorSensitivityLength, struct Wall * wall)  {
    //viewing_region of sensor is a square of 2 pixels * chosen length of sensitivity
    int overlap = checkOverlap(x,2,y,sensorSensitivityLength,
                    wall->x,wall->width,wall->y, wall->height);

    return overlap;
}

int checkRobotSensorFrontRightAllWalls(struct Robot * robot, struct Wall_collection * head) {
    struct Wall_collection *ptr, *head_store;
    int i;
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTR, yTR;
    int score, hit;

    int sensorSensitivityLength =  floor(SENSOR_VISION*sin(45)/20);

    head_store = head;
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;
    score = 0;

    for (i = 0; i < 20 ; i++)
    {
        ptr = head_store;
        xDir = round(robotCentreX+(ROBOT_WIDTH/2 + SENSOR_VISION*sin(40) - sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2 -SENSOR_VISION*sin(40) + sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180));
        yDir = round(robotCentreY+(ROBOT_WIDTH/2 + SENSOR_VISION*sin(40) - sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2 -SENSOR_VISION*sin(40) + sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180));

        xTR = (int) xDir;
        yTR = (int) yDir;
        hit = 0;

        while(ptr != NULL) {
            hit = (hit || checkRobotSensor(xTR, yTR, sensorSensitivityLength, &ptr->wall));
            ptr = ptr->next;
        }
        if (hit){
            score = i;
            // return score;
        }
    }
    return score;
}


int checkRobotSensorFrontLeftAllWalls(struct Robot * robot, struct Wall_collection * head) {
    struct Wall_collection *ptr, *head_store;
    int i;
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTL, yTL;
    int score, hit;

    int sensorSensitivityLength =  floor(SENSOR_VISION*sin(45)/20);

    head_store = head;
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;
    score = 0;

    for (i = 0; i < 20; i++)
    {
        ptr = head_store;
        xDir = round(robotCentreX+(-ROBOT_WIDTH/2 - SENSOR_VISION*sin(40) + sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2 - SENSOR_VISION*sin(40) + sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180));
        yDir = round(robotCentreY+(-ROBOT_WIDTH/2 - SENSOR_VISION*sin(40) + sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2 - SENSOR_VISION*sin(40) + sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180));
        xTL = (int) xDir;
        yTL = (int) yDir;
        hit = 0;

        while(ptr != NULL) {
            hit = (hit || checkRobotSensor(xTL, yTL, sensorSensitivityLength, &ptr->wall));
            ptr = ptr->next;
        }
        if (hit)
            score = i;
            // return score;
    }
    return score;
}

// Adding in short right sensor
int checkRobotSensorRightDiagonalAllWalls(struct Robot * robot, struct Wall_collection * head) {
    struct Wall_collection *ptr, *head_store;
    int i;
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTR, yTR;
    int score, hit;

    int sensorSensitivityLength =  floor(SENSOR_SHORT_VISION*sin(45)/5);

    head_store = head;
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;
    score = 0;

    for (i = 0; i < 5 ; i++)
    {
        ptr = head_store;
        xDir = round(robotCentreX+(ROBOT_WIDTH/2 + SENSOR_SHORT_VISION*sin(45) - sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180)-((-ROBOT_HEIGHT+10)/2 -SENSOR_SHORT_VISION*sin(45) + sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180));
        yDir = round(robotCentreY+(ROBOT_WIDTH/2 + SENSOR_SHORT_VISION*sin(45) - sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180)+((-ROBOT_HEIGHT+10)/2 -SENSOR_SHORT_VISION*sin(45) + sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180));
        xTR = (int) xDir;
        yTR = (int) yDir;
        hit = 0;

        while(ptr != NULL) {
            hit = (hit || checkRobotSensor(xTR, yTR, sensorSensitivityLength, &ptr->wall));
            ptr = ptr->next;
        }
        if (hit){
            score = i;
            // return score;
        }
    }
    return score;
}


int checkRobotSensorLeftDiagonalAllWalls(struct Robot * robot, struct Wall_collection * head) {
    struct Wall_collection *ptr, *head_store;
    int i;
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTL, yTL;
    int score, hit;

    int sensorSensitivityLength =  floor(SENSOR_SHORT_VISION*sin(45)/5);

    head_store = head;
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;
    score = 0;

    for (i = 0; i < 10; i++)
    {
        ptr = head_store;
        xDir = round(robotCentreX+(-ROBOT_WIDTH/2 - SENSOR_SHORT_VISION*sin(45) + sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180)-((-ROBOT_HEIGHT+10)/2 - SENSOR_SHORT_VISION*sin(45) + sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180));
        yDir = round(robotCentreY+(-ROBOT_WIDTH/2 - SENSOR_SHORT_VISION*sin(45) + sensorSensitivityLength*sin(45)*i)*sin((robot->angle)*PI/180)+((-ROBOT_HEIGHT+10)/2 - SENSOR_SHORT_VISION*sin(45) + sensorSensitivityLength*sin(45)*i)*cos((robot->angle)*PI/180));
        xTL = (int) xDir;
        yTL = (int) yDir;
        hit = 0;

        while(ptr != NULL) {
            hit = (hit || checkRobotSensor(xTL, yTL, sensorSensitivityLength, &ptr->wall));
            ptr = ptr->next;
        }
        if (hit)
            score = i;
            // return score;
    }
    return score;
}

int checkRobotSensorRight(struct Robot * robot, struct Wall_collection * head) {
    struct Wall_collection *ptr, *head_store;
    int i;
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTL, yTL;
    int score, hit;
    int sensorSensitivityLength;

    head_store = head;
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;
    score = 0;
    sensorSensitivityLength =  floor(SENSOR_VISION/20);

    for (i = 0; i < 20; i++)
    {
        ptr = head_store;
        xDir = round(robotCentreX+(+ROBOT_WIDTH/2)*cos((robot->angle+90)*PI/180)-(-ROBOT_HEIGHT/2-SENSOR_VISION+sensorSensitivityLength*i)*sin((robot->angle+90)*PI/180));
        yDir = round(robotCentreY+(+ROBOT_WIDTH/2)*sin((robot->angle+90)*PI/180)+(-ROBOT_HEIGHT/2-SENSOR_VISION+sensorSensitivityLength*i)*cos((robot->angle+90)*PI/180));
        xTL = (int) xDir;
        yTL = (int) yDir;
        hit = 0;

        while(ptr != NULL) {
            hit = (hit || checkRobotSensor(xTL, yTL, sensorSensitivityLength, &ptr->wall));
            ptr = ptr->next;
        }
        if (hit)
            score = i;
    }
    return score;
}

int checkRobotSensorLeft(struct Robot * robot, struct Wall_collection * head) {
    struct Wall_collection *ptr, *head_store;
    int i;
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTL, yTL;
    int score, hit;
    int sensorSensitivityLength;


    head_store = head;
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;
    score = 0;
    sensorSensitivityLength =  floor(SENSOR_VISION/20);

    for (i = 0; i < 20; i++)
    {
        ptr = head_store;
        xDir = round(robotCentreX+(-ROBOT_WIDTH/2)*cos((robot->angle+270)*PI/180)-(-ROBOT_HEIGHT/2-SENSOR_VISION+sensorSensitivityLength*i)*sin((robot->angle+270)*PI/180));
        yDir = round(robotCentreY+(-ROBOT_WIDTH/2)*sin((robot->angle+270)*PI/180)+(-ROBOT_HEIGHT/2-SENSOR_VISION+sensorSensitivityLength*i)*cos((robot->angle+270)*PI/180));
        xTL = (int) xDir;
        yTL = (int) yDir;
        hit = 0;

        while(ptr != NULL) {
            hit = (hit || checkRobotSensor(xTL, yTL, sensorSensitivityLength, &ptr->wall));
            ptr = ptr->next;
        }
        if (hit)
            score = i;
    }
    return score;
}


//FINISH

void robotUpdate(struct SDL_Renderer * renderer, struct Robot * robot,SDL_Surface * Speed1, SDL_Surface * Speed2, SDL_Surface * Speed3, SDL_Surface * Speed4, int front_left_sensor, int front_right_sensor, int left_short_sensor, int right_short_sensor, int left_sensor, int right_sensor ){
    double xDir, yDir;
    int robotCentreX, robotCentreY, xTR, yTR, xTL, yTL, xBR, yBR, xBL, yBL, xS1, yS1, xS2, yS2, xS3, yS3, xS4, yS4, xS5, yS5, xS6, yS6;
    // (xTR)
    SDL_SetRenderDrawColor(renderer, 100, 100, 100, 255);

    /*
    //Other Display options:
    // The actual square which the robot is tested against (not so nice visually with turns, but easier
    // to test overlap
    SDL_Rect rect = {robot->x, robot->y, robot->height, robot->width};
    SDL_SetRenderDrawColor(renderer, 80, 80, 80, 255);
    SDL_RenderDrawRect(renderer, &rect);
    SDL_RenderFillRect(renderer, &rect);
    */
    /*
    //Center Line of Robot. Line shows the direction robot is facing
    xDir = -30 * sin(-robot->angle*PI/180);
    yDir = -30 * cos(-robot->angle*PI/180);
    xDirInt = robot->x+ROBOT_WIDTH/2+ (int) xDir;
    yDirInt = robot->y+ROBOT_HEIGHT/2+ (int) yDir;
    SDL_RenderDrawLine(renderer,robot->x+ROBOT_WIDTH/2, robot->y+ROBOT_HEIGHT/2, xDirInt, yDirInt);
    */

    //Rotating Square
    //Vector rotation to work out corners x2 = x1cos(angle)-y1sin(angle), y2 = x1sin(angle)+y1cos(angle)
    robotCentreX = robot->x+ROBOT_WIDTH/2;
    robotCentreY = robot->y+ROBOT_HEIGHT/2;

    xDir = round(robotCentreX+(ROBOT_WIDTH/2)*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2)*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(ROBOT_WIDTH/2)*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2)*cos((robot->angle)*PI/180));
    xTR = (int) xDir;
    yTR = (int) yDir;

    xDir = round(robotCentreX+(ROBOT_WIDTH/2)*cos((robot->angle)*PI/180)-(ROBOT_HEIGHT/2)*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(ROBOT_WIDTH/2)*sin((robot->angle)*PI/180)+(ROBOT_HEIGHT/2)*cos((robot->angle)*PI/180));
    xBR = (int) xDir;
    yBR = (int) yDir;

    xDir = round(robotCentreX+(-ROBOT_WIDTH/2)*cos((robot->angle)*PI/180)-(ROBOT_HEIGHT/2)*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(-ROBOT_WIDTH/2)*sin((robot->angle)*PI/180)+(ROBOT_HEIGHT/2)*cos((robot->angle)*PI/180));
    xBL = (int) xDir;
    yBL = (int) yDir;

    xDir = round(robotCentreX+(-ROBOT_WIDTH/2)*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2)*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(-ROBOT_WIDTH/2)*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2)*cos((robot->angle)*PI/180));
    xTL = (int) xDir;
    yTL = (int) yDir;


    int short_sensor_sensitivity = floor(SENSOR_SHORT_VISION/5);

    /////////////////straight one sensors/////////////////

    SDL_SetRenderDrawColor(renderer,255,0,0,0);
    // //RIGHT Sensor (long)
    int sensorSensitivityLength =  floor(SENSOR_VISION/20);

    xDir = round(robotCentreX+(+ROBOT_WIDTH/2 +SENSOR_VISION - sensorSensitivityLength*right_sensor)*cos((robot->angle)*PI/180));
    yDir = round(robotCentreY+(+ROBOT_WIDTH/2 +SENSOR_VISION - sensorSensitivityLength*right_sensor)*sin((robot->angle)*PI/180));
    xS1 = (int) xDir;
    yS1 = (int) yDir;

    SDL_RenderDrawLine(renderer,xTR, yTR, xS1, yS1);

    // //Front Left Sensor long

    xDir = round(robotCentreX+(-ROBOT_WIDTH/2-SENSOR_VISION*sin(40) + sensorSensitivityLength* front_left_sensor *sin(40))*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2-SENSOR_VISION*sin(40) + sensorSensitivityLength* front_left_sensor *sin(40))*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(-ROBOT_WIDTH/2-SENSOR_VISION*sin(40) + sensorSensitivityLength* front_left_sensor *sin(40))*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2-SENSOR_VISION*sin(40) + sensorSensitivityLength* front_left_sensor *sin(40))*cos((robot->angle)*PI/180));
    xS2 = (int) xDir;
    yS2 = (int) yDir;

    SDL_RenderDrawLine(renderer,xTL, yTL, xS2, yS2);


    // //Front Right Sensor long

    xDir = round(robotCentreX+(ROBOT_WIDTH/2+SENSOR_VISION*sin(40) - sensorSensitivityLength* front_right_sensor *sin(40) )*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2-SENSOR_VISION*sin(40) + sensorSensitivityLength* front_right_sensor *sin(40))*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(ROBOT_WIDTH/2+SENSOR_VISION*sin(40) - sensorSensitivityLength* front_right_sensor *sin(40) )*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2-SENSOR_VISION*sin(40) + sensorSensitivityLength* front_right_sensor *sin(40) )*cos((robot->angle)*PI/180));
    xS3 = (int) xDir;
    yS3 = (int) yDir;

    SDL_RenderDrawLine(renderer,xTR, yTR, xS3, yS3);


    // //LEFT Sensor (long)

    xDir = round(robotCentreX+(-ROBOT_WIDTH/2 -SENSOR_VISION + sensorSensitivityLength* left_sensor)*cos((robot->angle)*PI/180));
    yDir = round(robotCentreY+(-ROBOT_WIDTH/2 -SENSOR_VISION + sensorSensitivityLength* left_sensor)*sin((robot->angle)*PI/180));
    xS6 = (int) xDir;
    yS6 = (int) yDir;

    SDL_RenderDrawLine(renderer,xTL, yTL, xS6, yS6);

    //short diagonal LEFT SENSOR

    SDL_SetRenderDrawColor(renderer,0,255,0,0);

    xDir = round(robotCentreX+(-ROBOT_WIDTH/2 - SENSOR_SHORT_VISION*sin(45) )*cos((robot->angle)*PI/180)-(-ROBOT_HEIGHT/2 - SENSOR_SHORT_VISION*sin(45))*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(-ROBOT_WIDTH/2 - SENSOR_SHORT_VISION*sin(45) )*sin((robot->angle)*PI/180)+(-ROBOT_HEIGHT/2 - SENSOR_SHORT_VISION*sin(45))*cos((robot->angle)*PI/180));
    xS4 = (int) xDir;
    yS4 = (int) yDir;
    SDL_RenderDrawLine(renderer,xTL, yTL, xS4, yS4);

    // short diagonal right sensor

    xDir = round(robotCentreX+(ROBOT_WIDTH/2 + SENSOR_SHORT_VISION*sin(45))*cos((robot->angle)*PI/180)-((-ROBOT_HEIGHT+10)/2 -SENSOR_SHORT_VISION*sin(45))*sin((robot->angle)*PI/180));
    yDir = round(robotCentreY+(ROBOT_WIDTH/2 + SENSOR_SHORT_VISION*sin(45))*sin((robot->angle)*PI/180)+((-ROBOT_HEIGHT+10)/2 -SENSOR_SHORT_VISION*sin(45))*cos((robot->angle)*PI/180));
    xS5 = (int) xDir;
    yS5 = (int) yDir;

    SDL_RenderDrawLine(renderer,xTR, yTR, xS5, yS5);


    /////////////////straight line sensors/////////////////

    ////////////load and draw the robot///////////////

    SDL_Rect dstrect;
    dstrect.x = robotCentreX-ROBOT_WIDTH/2-12;
    dstrect.y = robotCentreY-ROBOT_HEIGHT/2-8;
    dstrect.w = ROBOT_WIDTH+24;
    dstrect.h = ROBOT_HEIGHT+16;

    //red light if current speed less equal 5
    if (robot->currentSpeed <= 2){
        SDL_Texture * Robot_texture = SDL_CreateTextureFromSurface(renderer, Speed1);
        SDL_RenderCopyEx(renderer,
                   Robot_texture,
                   NULL,
                   &dstrect,
                   robot->angle,
                   NULL,
                   SDL_FLIP_NONE);
    } //orange light if current speed less equal 10
    else if (robot->currentSpeed <= 3){
        SDL_Texture * Robot_texture = SDL_CreateTextureFromSurface(renderer, Speed2);
        SDL_RenderCopyEx(renderer,
                   Robot_texture,
                   NULL,
                   &dstrect,
                   robot->angle,
                   NULL,
                   SDL_FLIP_NONE);
    }//red yellow if current speed less equal 15
    else if (robot->currentSpeed <= 5){
        SDL_Texture * Robot_texture = SDL_CreateTextureFromSurface(renderer, Speed3);
        SDL_RenderCopyEx(renderer,
                   Robot_texture,
                   NULL,
                   &dstrect,
                   robot->angle,
                   NULL,
                   SDL_FLIP_NONE);
    }//green light if current speed less equal 25
    else if (robot->currentSpeed <= 25){
        SDL_Texture * Robot_texture = SDL_CreateTextureFromSurface(renderer, Speed4);
        SDL_RenderCopyEx(renderer,
                   Robot_texture,
                   NULL,
                   &dstrect,
                   robot->angle,
                   NULL,
                   SDL_FLIP_NONE);
    }
    
    ////////////load and draw the robot///////////////
    //FINISH
}
    

void robotMotorMove(struct Robot * robot) {
    double x_offset, y_offset;
    switch(robot->direction){
        case UP :
            robot->currentSpeed += DEFAULT_SPEED_CHANGE;
            if (robot->currentSpeed > MAX_ROBOT_SPEED)
                robot->currentSpeed = MAX_ROBOT_SPEED;
            break;
        case DOWN :
            robot->currentSpeed -= DEFAULT_SPEED_CHANGE;
            if (robot->currentSpeed < -MAX_ROBOT_SPEED)
                robot->currentSpeed = -MAX_ROBOT_SPEED;
            break;
        case LEFT :
            robot->angle = (robot->angle+360-DEFAULT_ANGLE_CHANGE)%360;
            break;
        case RIGHT :
            robot->angle = (robot->angle+DEFAULT_ANGLE_CHANGE)%360;
            break;
    }
    robot->direction = 0;
    x_offset = (-robot->currentSpeed * sin(-robot->angle*PI/180));
    y_offset = (-robot->currentSpeed * cos(-robot->angle*PI/180));

    robot->true_x += x_offset;
    robot->true_y += y_offset;

    x_offset = round(robot->true_x);
    y_offset = round(robot->true_y);

    robot->x = (int) x_offset;
    robot->y = (int) y_offset;
}

void robotAutoMotorMove(struct Robot * robot, int front_right_sensor, int front_left_sensor, int short_left, int short_right, int right_sensor, int left_sensor) {

 if (robot-> currentSpeed < 3){
        // 15 highest, 3 for normal
            robot->direction = UP;
    }

    else if (short_right ==0  && short_left == 0){
        if (front_left_sensor == front_right_sensor && (front_left_sensor >= 13 || front_right_sensor >=13)){
            if (right_sensor > left_sensor){
                printf("side sensor left \n");
                robot->direction = RIGHT;

            }else if (right_sensor < left_sensor){
                printf("side sensor right \n");
                robot->direction = LEFT;
                
            } 
            // comment this part for normal
            else if (robot->currentSpeed < 9)  {
                robot->direction = UP;
            }
            
        }
        else if (front_left_sensor < front_right_sensor ){
            printf("front sensor left \n");
            robot->direction = LEFT;
        } else if (front_left_sensor > front_right_sensor){
            printf("front sensor right \n");
            robot->direction = RIGHT;
        } 
        ///////////20 s 
        
        else if (robot->currentSpeed < 24)  { 
            //can change to 24 for basic and default maze, 10 for other maze
                robot->direction = UP;
            }
        
    } else {

        
        if (short_left > short_right){
            printf("short sensor right \n");
            robot->direction = RIGHT;
        } else if (short_left < short_right){
            printf("short sensor left \n");
            robot->direction = LEFT;
        } else if (robot->currentSpeed < 24 ){
            // 24 highest, 9 for normal
            printf("short sensor up \n");
            robot->direction = UP;
        }
        
    }
    // comment this part for maximum
    if ((front_right_sensor >= 10 || front_left_sensor >= 10 )&& robot->currentSpeed > 10){
        printf("deccelerate \n");
        robot->direction = DOWN;
    }
    
        
}