const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

const DB = admin.firestore();
 
exports.createLibros = functions.firestore
	.document('aa/{aaId}')
	.onCreate((snap, context) => {
		
	console.info("---- PROCESO INIT -----");
 

	const data = snap.data();
	const uid = context.params.aaId


	console.info("id:" + uid);
	console.info("data Demo:" + data);

	const arrPasaje = uid.split("_");

	const libro = arrPasaje[0];
	const _capitulo = parseInt(arrPasaje[1]);
	const _versiculo = parseInt(arrPasaje[2]);

	const libroFirstMayus = (libro.charAt(0).toUpperCase() + libro.slice(1)).replace("_"," ");

	const refLibros = DB.doc('libros/'+libro);


	DB.doc('libros/'+libro).set({ nombre: libroFirstMayus }, { merge: true })
	.then(() => {
		console.info("libros ha sido creado! " + libro);
		
		DB.doc('versiculos/'+uid).set({ 
										capitulo: _capitulo,
										libros: refLibros,
										versiculo: _versiculo
										}, { merge: true })
			.then(() => {
				console.info("Versiculo ha sido creado! " + uid);
				DB.doc('versiculos/'+uid+'/versiones/RVR1960').set({ 
										texto: data['texto']
										}, { merge: true })
					.then(() => {
						console.info("Texto versiculo ha sido creado! " + data['texto']);
						return null;
					})
					.catch((error) => {
						console.error("Texto versiculo Error al ser creado: ", error);
						return null;
					});
				return null;
			})
			.catch((error) => {
				console.error("Versiculo Error al ser creado: ", error);
				return null;
			});
		
		
		return null;
	})
	.catch((error) => {
		console.error("Libros Error al ser creado: ", error);
		return null;
	});
	
	console.info("---- PROCESO TERMINADO -----");
	return null;
});


//actualizar nombres de usuario 
exports.updateUser = functions.firestore
	.document('usuarios/{userId}')
    .onUpdate((change, context) => {
		
	console.info("---- PROCESO INIT -----");
 

	const newValue = change.after.data();
	const userId = context.params.userId;

	console.info("userId:" + userId);


	let usuariosRef = DB.collection('usuarios');
	let allUsuarios = usuariosRef.get()
	.then(snapshot => {
		snapshot.forEach(doc => {
			//descarta el usuario actual
			if(userId !== doc.id){
				DB.doc('usuarios/'+doc.id+'/amigos/'+userId)
					.get()
					.then(doc2 => {
						if (doc2.exists) {
							
							DB.doc('usuarios/'+doc.id+'/amigos/'+userId).set(newValue, { merge: true })
								.then(() => {
									console.info("OK Usuario Actualizado!");
									return null;
								})
								.catch((error) => {
									console.error("Error Usuario Actualizado: ", error);
									return null;
								});
						}
					})
					.catch(err => {
						console.info('Error consultan document amigo', err);
					});
			}

		});
	})
	.catch(err => {
		console.info('Error getting documents', err);
	});
	
	console.info("---- PROCESO TERMINADO -----");
	return null;
});



//eliminar dispositivos 
exports.deleteDeviceRepeat = functions.firestore
	.document('usuarios/{userId}/dispositivos/{deviceId}')
    .onCreate((snap, context) => {
	
		console.info("---- PROCESO INIT -----");
 

	const newValue = snap.data();
	const userId = context.params.userId;
	const deviceId = context.params.deviceId;

	console.info("userId:" + userId);
	console.info("deviceId:" + deviceId);


	let usuariosRef = DB.collection('usuarios');
	let allUsuarios = usuariosRef.get()
	.then(snapshot => {
		snapshot.forEach(doc => {
			//descarta el usuario actual
			if(userId !== doc.id){

				DB.doc('usuarios/'+doc.id+'/dispositivos/'+deviceId)
					.get()
					.then(doc2 => {
						if (doc2.exists) {
							
							DB.doc('usuarios/'+doc.id+'/dispositivos/'+deviceId)
								.delete()
								.then(() => {
									console.log("Dispositivo Eliminado!");
									return null;
								})
								.catch((error) => {
									console.error("Error Dispositivo Eliminado: ", error);
									return null;
								});
						}
					})
					.catch(err => {
						console.log('Error consultan document dispositivo', err);
					});
			}

		});
	})
	.catch(err => {
		console.log('Error getting documents', err);
	});
	
	console.info("---- PROCESO TERMINADO -----");
	return null;
});


exports.pushNotificationAccept = functions.firestore
	.document('usuarios/{userId}/amigos/{friendId}')
    .onUpdate((change, context) => {

	console.info('----INICIO-----');

	const oldValue = change.before.data();
	const newValue = change.after.data();
	const userId = context.params.userId;


	if(newValue['amigo'] && !oldValue['amigo'] && newValue['ienvie']){

		console.log('User to send notification', userId);

		let usuariosRef = DB.collection('usuarios/'+userId+'/dispositivos/');
		usuariosRef.get()
		.then(snapshot => {
			snapshot.forEach(token => {

				const payload = {
					notification: {
						title: 'Notificación de Amistad.',
						body: newValue['nombre']+' > Acepto su solicitud de amistad'
					}
				};
				
				admin.messaging().sendToDevice(token.id, payload)
				.then((response) => {
					if(response.failureCount >= 1){
						console.log("ha borrar device: ", response);
						DB.doc('usuarios/'+userId+'/dispositivos/'+token.id)
									.delete()
									.then(() => {
										console.log("Dispositivo "+token.id+" Eliminado!");
										return null;
									})
									.catch((error) => {
										console.error("Error Dispositivo Eliminado: ", error);
										return null;
									});
					}
					console.log("Successfully sent message: ", response);
					return true;
				})
				.catch((error) => {
					console.log("Error sending message: ==='"+token.id+"'===", error);
					return false;
				})
			});
		})
		.catch(err => {
			console.info('Error getting collections', err);
		});
	}
		
	console.info("---- PROCESO TERMINADO -----");
	return true;
});


exports.pushNotificationNew = functions.firestore
	.document('usuarios/{userId}/amigos/{friendId}')
    .onCreate((snap, context) => {

	console.info('----INICIO-----');

	const newValue = snap.data();
	const userId = context.params.userId;


	if(!newValue['amigo'] && !newValue['ienvie']){

		console.log('User to send notification', userId);

		let usuariosRef = DB.collection('usuarios/'+userId+'/dispositivos/');
		usuariosRef.get()
		.then(snapshot => {
			snapshot.forEach(token => {

				const payload = {
					notification: {
						title: 'Notificación de Amistad.',
						body: newValue['nombre']+' ha enviado una solicitud de amistad'
					}
				};
				
				admin.messaging().sendToDevice(token.id, payload)
				.then((response) => {
					if(response.failureCount >= 1){
						console.log("ha borrar device: ", response);
						DB.doc('usuarios/'+userId+'/dispositivos/'+token.id)
									.delete()
									.then(() => {
										console.log("Dispositivo "+token.id+" Eliminado!");
										return null;
									})
									.catch((error) => {
										console.error("Error Dispositivo Eliminado: ", error);
										return null;
									});
					}
					console.log("Successfully sent message: ", response);
					return true;
				})
				.catch((error) => {
					console.log("Error sending message: ==='"+token.id+"'===", error);
					return false;
				})
			});
		})
		.catch(err => {
			console.info('Error getting collections', err);
		});
	}
		
	console.info("---- PROCESO TERMINADO -----");
	return true;
});