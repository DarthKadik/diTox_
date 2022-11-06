from flask import Flask, request
from flask_socketio import SocketIO, join_room, send, leave_room, emit
import secrets
import sqlite3


con = sqlite3.connect("database/database.db", check_same_thread=False)
cur = con.cursor()
cur.execute('''SELECT name FROM sqlite_master WHERE type='table' AND name='messages';''')

if cur.fetchone() is None:
    with open('database/schema.sql') as fp:
        cur.executescript(fp.read())

app = Flask(__name__)

app.config['SECRET_KEY'] = 'vnkdjnfjknfl1232#'
socketio = SocketIO(app)
connected_rooms = set()

def verify_sig(data, type_identifier):
    # verify challenge with registered public key
    content = str(data[type_identifier])
    cid = data['client_id']
    signature = data['signature']
    # cur.execute(f"SELECT pk FROM users WHERE id IS {cid}")
    # pk = cur.fetchone()
    # compare signature and content

    return True

@app.route('/get_challenge/<cid>', methods=['GET'])
def send_challenge(cid):
    chall = secrets.token_urlsafe(32)
    return str(chall)

@socketio.on('send_to_id')
def send_to_id(data):
    origin = data['client_id']
    destination = data['peer_id']
    if destination in connected_rooms:
        if verify_sig(data, "message"):
            message = data['message']
            send(f"{origin}: {message}", to=destination)
    else:
        if verify_sig(data, "message"):
            print("Storing message")
            cur.execute(f"INSERT INTO messages ( message, client_id, peer_id, time_stamp, message_id) VALUES ('{data['message']}', '{data['peer_id']}', '{data['client_id']}',{data['time_stamp']}, '{data['message_id']}')")
            con.commit()

@socketio.on('join')
def on_join(data):
    cid = data['client_id']
    if verify_sig(data, "challenge"):
        join_room(cid)
        connected_rooms.add(cid)
        emit("sig_success", to=cid)

    else:
        connected_rooms.add(cid)
        emit("sig_incorrect", to=cid)
        leave_room(cid)

@socketio.on('leave')
def on_leave(data):
    if verify_sig(data, "challenge"):
        cid = data['client_id']
        connected_rooms.remove(cid)
        print(connected_rooms)
        emit("leave_success", to=cid)
        leave_room(cid)


@socketio.on('message')
def handle_message(data):
    print('received message: ' + data)

@socketio.on('json')
def handle_json(json):
    print('received json: ' + str(json))

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    cur.execute(f"SELECT 1 FROM users WHERE id IS '{data['client_id']}'")
    if cur.fetchone() is None:
        cur.execute(f"INSERT INTO users (id, pk) VALUES ('{data['client_id']}', '{data['public_key']}')")
        con.commit()
        return "Registered"
    return "client id already exists"

@app.route('/get_messages', methods=['POST'])
def return_messages():
    print("getting messages")
    data = request.get_json()
    if verify_sig(data, 'challenge'):
        cur.execute(f" SELECT message_id, time_stamp, message FROM messages WHERE client_id = '{data['client_id']}' AND peer_id = {data['peer_id']}")
        results = cur.fetchall()
        ret = []
        for row in results:
            ret.append({"message_id": row[0], "timestamp": row[1], "message": row[2]})
        return ret

if __name__ == '__main__':
    app.run()
