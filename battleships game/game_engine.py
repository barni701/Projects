import components as cm

def attack(coordinates:tuple, board:list, battleships:dict) -> bool:

    '''
    cheks if a given coordinate has a ship on it, if there is it prints "HIT" and sets that cell's value to None, if not "MISS"
    '''
    #cheks if there is a ship on a cell
    if board[coordinates[1]][coordinates[0]] is not None:
        battleships[board[coordinates[1]][coordinates[0]]] -= 1
        print("HIT")

        #checks if a ship has sunken
        if battleships[board[coordinates[1]][coordinates[0]]] == 0:
            print("ship sunk")

        board[coordinates[1]][coordinates[0]] = None
        return True

    else:
        print("MISS")
        return False



def cli_coordinates_input() -> tuple:
    '''
    asks for a input for the x and y axis between 0 and board-size
    '''
    try:
        coordinate1 = int(input("where do you want to shoot? x-coord "))
        coordinate2 = int(input("where do you want to shoot? y-coord "))


        while coordinate1 < 0 or coordinate1 > len(cm.initialise_board()) or coordinate2 < 0 or coordinate2 > len(cm.initialise_board()):
            print("That is not a valid coordinate, please type in a new one \n")

            coordinate1 = int(input("where do you want to shoot? x-coord "))
            coordinate2 = int(input("where do you want to shoot? y-coord "))

    except ValueError:
        raise ValueError("int expected but str given")

    coordinates = coordinate2, coordinate1

    return coordinates


def simple_game_loop():
    print("Hello \npress enter to play")
    input()

    #setting up the board
    ini_board = cm.initialise_board()
    board = cm.place_battleships(board=ini_board, algorithm="random")
    dict_of_ships = cm.create_battleships()


    while True:

        shoot_to = cli_coordinates_input()
        attack(shoot_to, board=board, battleships=dict_of_ships)

        summ = 0
        for key in dict_of_ships:
            summ = summ + dict_of_ships[key]

        if summ == 0:
            print("game over")
            break
