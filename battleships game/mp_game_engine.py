import random
import components as cm
import game_engine as ge



players = {"user": [cm.place_battleships(board=cm.initialise_board(), algorithm="custom"), cm.create_battleships()],
           "AI": [cm.place_battleships(board=cm.initialise_board(), algorithm="random"), cm.create_battleships()]}

previous_Shots = []
shots_hit = []
next_shots = []


def generate_intelligent_attack(playerboard:list) -> tuple:
    '''
    At first generates a shot randomly, and if it hits it shoots around that coordinate
    stores the previous shots / shots that hit / upcoming shots in lists

    playerboard: function will shoot onto this board (list of lists)
    '''
    global next_shots
    global previous_Shots
    global shots_hit

    #if there are no preprogrammed shots, then random shots
    if len(next_shots) == 0:

        shooting_cords = random.randint(0,len(cm.initialise_board())-1), random.randint(0,len(cm.initialise_board())-1)

        while shooting_cords in previous_Shots:
            shooting_cords = random.randint(0,len(cm.initialise_board())-1), random.randint(0,len(cm.initialise_board())-1)

        previous_Shots.append(shooting_cords)


        #if there is a hit save the adjacent cells
        if playerboard[shooting_cords[1]][shooting_cords[0]] is not None:

            next_shots.clear()

            if shooting_cords[0]+1 < len(playerboard):
                next_shots.append((shooting_cords[0]+1, shooting_cords[1]))

            if shooting_cords[1] +1 < len(playerboard):
                next_shots.append((shooting_cords[0], shooting_cords[1]+1))
                
            if shooting_cords[0]-1 >= 0:
                next_shots.append((shooting_cords[0]-1, shooting_cords[1]))

            if shooting_cords[1]-1 >= 0:
                next_shots.append((shooting_cords[0], shooting_cords[1]-1))


            next_shots = [element for element in next_shots if element not in previous_Shots]

    #if there are preprogrammed shots, shoot to those cells 
    else:
        
        shooting_cords = next_shots[0]

        next_shots.pop(0)

        previous_Shots.append(shooting_cords)

        if playerboard[shooting_cords[1]][shooting_cords[0]] is not None:

            if shooting_cords[0]+1 < len(playerboard):
                next_shots.append((shooting_cords[0]+1, shooting_cords[1]))

            if shooting_cords[1] +1 < len(playerboard):
                next_shots.append((shooting_cords[0], shooting_cords[1]+1))
                
            if shooting_cords[0]-1 >= 0:
                next_shots.append((shooting_cords[0]-1, shooting_cords[1]))

            if shooting_cords[1]-1 >= 0:
                next_shots.append((shooting_cords[0], shooting_cords[1]-1))


            next_shots = [element for element in next_shots if element not in previous_Shots]

    return shooting_cords



def cheat_shot(playerboard) -> tuple:
    '''
    pulls the coordinates of the ships and shoot to them
    '''
    for row in range(len(playerboard)):
        for col in range(len(playerboard)):
            if playerboard[row][col] is not None:
                next_shots.append((col, row))

    coord = next_shots[0]
    next_shots.remove(coord)

    return coord


def difficulty(board, dif = "hard") -> tuple:
    '''
    select shooting algorithm based on the passed in difficulty
    board: function will shoot onto this board (list of lists)
    dif: easy / hard / impossible
    '''
    if dif == "easy":
        return generate_attack()
    if dif == "hard":
        return generate_intelligent_attack(playerboard=board)
    if dif == "impossible":
        return cheat_shot(playerboard=board)


def generate_attack() -> tuple:
    '''
    generates random shots onto coordinates, which have not yet been used
    '''
    
    shooting_cords = random.randint(0,len(cm.initialise_board())-1), random.randint(0,len(cm.initialise_board())-1)

    while shooting_cords in previous_Shots:
        shooting_cords = random.randint(0,len(cm.initialise_board())-1), random.randint(0,len(cm.initialise_board())-1)

    previous_Shots.append(shooting_cords)

    return shooting_cords



def ai_opponent_game_loop():
    print("Hello \npress enter to play")
    input()

    #setting up the board
    dict_of_player_ships = players.get("user")[1]
    dict_of_ai_ships = players.get("AI")[1]
    ai_board = players.get("AI")[0]
    player_board = players.get("user")[0]



    while True:

        #ask for coordinates and shoot to them 
        cordinates = ge.cli_coordinates_input()
        ge.attack(cordinates, ai_board, dict_of_ai_ships)

        #generate opponents turn
        print("\nOpponent's turn:")
        ge.attack(generate_attack(), player_board, dict_of_player_ships)

        playersum = 0
        for key in dict_of_player_ships:
            playersum = playersum + dict_of_player_ships[key]

        ai_sum = 0
        for key in dict_of_ai_ships:
            ai_sum = ai_sum + dict_of_ai_ships[key]

        #check whether someone has lost
        if ai_sum == 0:
            print("Game over \nWinner: user")
            break

        if playersum == 0:
            print("Game Over \nWinner: AI")
            break
