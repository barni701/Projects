import random
import json

def initialise_board(size = 10) -> list:
    '''
    makes a list of lists based on the size given
    size: board length
    '''
    board =[]
    for i in range(size):
        row =[]
        for j in range(size):
            row.append(None)
        board.append(row)

    return board


def create_battleships(filename = "battleships.txt") -> dict:

    '''
    opens a txt file and makes a dictionary out of it containing the names and the lengths of the ships
    '''

    ships = open(filename, "r", encoding = "UTF-8").readlines()

    battleships = {}

    for i in range(0,len(ships)-1,2):

        newship = ships[i].strip("\n")

        battleships.update({newship:int(ships[i+1])})


    return battleships


def place_battleships_random(board:list, ships:dict) -> list:

    '''
    randomly assigns the ships on the board without any collisions

    board: this board is where the ships will be placed on (list of lists)
    ships: the values of the ships will be pulled from this dictionary
    '''


    for key, value in ships.items():
        #random orientation
        orientation = random.choice(["horizontal", "vertical"])

        #calculating where the ship's front / end is if orientation is horizontal
        if orientation == 'horizontal':
            start_row = random.randint(0, len(board)-1)
            start_coloumn = random.randint(0, len(board)- value-1)

            end_row = start_row
            end_coloumn = start_coloumn + value - 1

            #checking for collisions
            while any(board[start_row][col] is not None for col in range(start_coloumn, end_coloumn+1)):

                start_row = random.randint(0, len(board)-1)
                start_coloumn = random.randint(0, len(board)- value-1)

                end_row = start_row
                end_coloumn = start_coloumn + value - 1

            for i in range(start_coloumn,end_coloumn+1):
                board[start_row][i] = key


        else:
            #calculating where the ship's front / end is if orientation is vertical
            start_row = random.randint(0, len(board)- value -1)
            start_coloumn = random.randint(0, len(board)- 1)

            end_row = start_row + value - 1
            end_coloumn = start_coloumn

            #checking for collisions
            while any(board[row][start_coloumn] is not None for row in range(start_row, end_row+1)):
                start_row = random.randint(0, len(board)- value -1)
                start_coloumn = random.randint(0, len(board)- 1)

                end_row = start_row + value - 1
                end_coloumn = start_coloumn

            for i in range(start_row, end_row+1):
                board[i][start_coloumn] = key

    return board


def place_battleships_json(dict_of_ships:dict, board:list, ships:dict) -> list:

    '''
    places the ships based on the json file

    dict_of_ships: a dictionary made from the json file (names: coordinates)
    board: on this board will the ships be placed (list of lists)
    ships: the values of the sihps are pulled from this dictionary
    '''

    #iterating through the ships (name, value, orientation)
    for key, value in dict_of_ships.items():

        start_row = int(dict_of_ships.get(key)[1])
        start_coloumn = int(dict_of_ships.get(key)[0])
        orientation = dict_of_ships.get(key)[2]
        length = int(ships.get(key))

        #ship's position if it is horizontal
        if orientation =="h":
            end_coloumn = start_coloumn + length-1
            end_row = start_row

            for i in range(start_coloumn,end_coloumn+1):
                board[start_row][i] = key

        #ship's position if it is vertical
        else:
            end_coloumn = start_coloumn
            end_row = start_row + length -1

            for i in range(start_row, end_row+1):
                board[i][start_coloumn] = key

    return board




def place_battleships(board, ships:dict = create_battleships(), algorithm = "simple") -> list:

    '''
    places the ships based on the algorithm chosen
    board: the boards will be placed on this board (list of lists)
    ships: dictionary with the names and values of the ships
    algorithm: simple / random / custom
    '''

    if algorithm == "simple":
        row = 0
        for key, value in ships.items():
            for i in range(value):
                board[row][i] = key

            row +=1

        return board

    elif algorithm == "random":
        board = initialise_board()
        ships = create_battleships()
        return place_battleships_random(board=board, ships=ships)

    elif algorithm == "custom":
        data = open("placement.json", encoding = "UTF-8")
        dict_of_ships = json.load(data)
        board = initialise_board()
        ships = create_battleships()
        return place_battleships_json(dict_of_ships=dict_of_ships, board=board, ships=ships)


def display_board(players: dict, player:str) -> list:

    '''
    makes the board a bit more readable (just te first letter of the value in the cell)
    just for testing purposes
    '''

    board_list = []

    for row in players.get(player)[0]:
        row_list = []
        for element in row:
            if element is None:
                row_list.append("N")
            else:
                row_list.append(element[0])

        board_list.append(row_list)

    for element in board_list:
        print(element, "\n")
