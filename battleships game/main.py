from flask import Flask, render_template, request, jsonify
import mp_game_engine as mpge
import components as cm
import game_engine as ge

app = Flask(__name__)


player_board = None

@app.route("/placement", methods =["GET", "POST"])
def placement_interface():
    '''
    ship placement with clicks 
    '''
    global player_board

    if request.method == 'GET':
        return render_template('placement.html', ships=cm.create_battleships(), board_size = len(cm.initialise_board()))


    if request.method == "POST":
        
        player_board = request.get_json()

        return jsonify({'message': 'Received'}), 200


@app.route("/", methods = ["GET"])
def root():
    global player_board
    if request.method =="GET":

        ini_board = cm.initialise_board()
        ships = cm.create_battleships()
        dict_of_ships = player_board


        player_board = cm.place_battleships_json(dict_of_ships = dict_of_ships, board= ini_board, ships=ships)

        return render_template('main.html', player_board=player_board)


@app.route("/attack", methods = ["GET"])
def process_attack():
    '''
    Handles turns between the player and the "AI"
    returns AI's shooting coordinates and whether player's shot hit a ship, if game ends it also returns a geme-end-message
    '''
    global player_board
    hit = False
    AI_Board = mpge.players.get("AI")[0]

    #get coordinate form click
    x = int(request.args.get('x'))
    y = int(request.args.get('y'))

    if AI_Board[y][x] is not None:
        hit = True

    else:
        hit = False

    #shoot to AI board, Ai shoots back 
    ge.attack((x,y), AI_Board, mpge.players.get("AI")[1])

    AI_Turn = mpge.difficulty(board=player_board, dif="hard")

    ge.attack(AI_Turn, player_board, mpge.players.get("user")[1])


    dict_of_player_ships = mpge.players.get("user")[1]
    dict_of_ai_ships = mpge.players.get("AI")[1]


    #check if a player still has ships or not
    Playersum = 0
    for key in dict_of_player_ships:
        Playersum = Playersum + dict_of_player_ships[key]

    AIsum = 0
    for key in dict_of_ai_ships:
        AIsum = AIsum + dict_of_ai_ships[key]


    if AIsum == 0:
        return jsonify({"hit": hit,
                        'AI_Turn': AI_Turn, 
                        "finished": "Game Over Player wins"})

    if Playersum == 0:
        return jsonify({'hit': hit,
        'AI_Turn': AI_Turn,
        'finished': "Game Over AI wins"
        })

    else:
        return jsonify({'hit': hit,
        'AI_Turn': AI_Turn
        })


if __name__ == "__main__":
    app.run()
