package com.example.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.grpc.Crud.CreateRequest;
import com.example.grpc.Crud.DeleteRequest;
import com.example.grpc.Crud.Employee;
import com.example.grpc.Crud.GetRequest;
import com.example.grpc.Crud.UpdateRequest;
import com.example.grpc.CrudServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcServiceApplication 
{
	private final int port;
	private final Server server;
	
	public GrpcServiceApplication() throws IOException {
		port = 8087;
		server = ServerBuilder.forPort(port)
				.addService(new CrudServiceImpl())
				.build()
				.start();
	}

	public void start() throws IOException 
	{
		//server.start();		
		System.out.println("Server Started listening on port: "+port);
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}
	
	public void stop()
	{
		if(server!=null)
		{
			server.shutdown();
		}
	}
	
	public void blockUntilShutdown() throws InterruptedException
	{
		if(server!=null)
		{
			server.awaitTermination();
		}
	}

	public static void main (String[] args) throws IOException, InterruptedException
	{
		System.out.println("Inside Service application");
		
		GrpcServiceApplication server = new GrpcServiceApplication();
		
		server.start();
		server.blockUntilShutdown();
	}
	
	private static class CrudServiceImpl extends CrudServiceGrpc.CrudServiceImplBase
	{
		private final Map<String, Employee> employeeMap = new HashMap<>();
		
		@Override
		public void create(CreateRequest request, StreamObserver<Employee> responseObserver) {
			String Id = generatedId();
			Employee createdEmployee = Employee.newBuilder()
					.setId(Id)
					.setName(request.getName())
					.setRole(request.getRole())
					.build();
			
			employeeMap.put(Id, createdEmployee);
			
			responseObserver.onNext(createdEmployee);
			responseObserver.onCompleted();
		}

		@Override
		public void update(UpdateRequest request, StreamObserver<Employee> responseObserver) {
			String Id = request.getId();
			Employee existingEmployee = employeeMap.get(Id);
			
			if(existingEmployee!=null)
			{
			Employee updatedEmployee = Employee.newBuilder()
					.setId(request.getId())
					.setName(request.getName())
					.setRole(request.getRole())
					.build();
			
			employeeMap.put(Id, updatedEmployee);
			
			responseObserver.onNext(updatedEmployee);
			responseObserver.onCompleted();
			}
			else
			{
				responseObserver.onError(new RuntimeException("Unable to find Id: "+Id));
			}
		}

		@Override
		public void delete(DeleteRequest request, StreamObserver<Employee> responseObserver) {
			String Id = request.getId();
			Employee removedEmployee = employeeMap.remove(Id);
			
			if(removedEmployee!=null)
			{
			responseObserver.onNext(removedEmployee);
			responseObserver.onCompleted();
			}
			else
			{
				responseObserver.onError(new RuntimeException("Unable to find Id: "+Id));
			}
		}

		@Override
		public void get(GetRequest request, StreamObserver<Employee> responseObserver) {
			String Id = request.getId();
			Employee retrievedEmployee = employeeMap.get(Id);
			
			if(retrievedEmployee!=null)
			{
			responseObserver.onNext(retrievedEmployee);
			responseObserver.onCompleted();
			}
			else
			{
				responseObserver.onError(new RuntimeException("Unable to find Id: "+Id));
			}
		}
		
		public String generatedId()
		{
			return String.valueOf(employeeMap.size()+1);
		}
	}
}
