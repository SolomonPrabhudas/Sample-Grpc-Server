//package Server;
//
//package com.example.grpc;
//
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import io.grpc.stub.StreamObserver;
//
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class CrudServer {
//    private final int port;
//    private final Server server;
//
//    public CrudServer() {
//        this.port = 50051;
//        this.server = ServerBuilder.forPort(port)
//                .addService(new CrudServiceImpl())
//                .build();
//    }
//
//    public void start() throws IOException {
//        server.start();
//        System.out.println("gRPC server started on port: " + port);
//        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
//    }
//
//    public void stop() {
//        if (server != null) {
//            server.shutdown();
//        }
//    }
//
//    public void blockUntilShutdown() throws InterruptedException {
//        if (server != null) {
//            server.awaitTermination();
//        }
//    }
//
//    private static class CrudServiceImpl extends CrudServiceGrpc.CrudServiceImplBase {
//        private final Map<String, Employee> employeeMap = new HashMap<>();
//
//        @Override
//        public void create(CreateRequest request, StreamObserver<Employee> responseObserver) {
//            String id = generateId();
//            Employee employee = Employee.newBuilder()
//                    .setId(id)
//                    .setName(request.getName())
//                    .setRole(request.getRole())
//                    .build();
//
//            employeeMap.put(id, employee);
//
//            responseObserver.onNext(employee);
//            responseObserver.onCompleted();
//        }
//
//        @Override
//        public void update(UpdateRequest request, StreamObserver<Employee> responseObserver) {
//            String id = request.getId();
//            Employee existingEmployee = employeeMap.get(id);
//
//            if (existingEmployee != null) {
//                Employee updatedEmployee = Employee.newBuilder()
//                        .setId(id)
//                        .setName(request.getName())
//                        .setRole(request.getRole())
//                        .build();
//
//                employeeMap.put(id, updatedEmployee);
//
//                responseObserver.onNext(updatedEmployee);
//                responseObserver.onCompleted();
//            } else {
//                responseObserver.onError(new RuntimeException("Employee not found with id: " + id));
//            }
//        }
//
//        @Override
//        public void delete(DeleteRequest request, StreamObserver<Employee> responseObserver) {
//            String id = request.getId();
//            Employee removedEmployee = employeeMap.remove(id);
//
//            if (removedEmployee != null) {
//                responseObserver.onNext(removedEmployee);
//                responseObserver.onCompleted();
//            } else {
//                responseObserver.onError(new RuntimeException("Employee not found with id: " + id));
//            }
//        }
//
//        @Override
//        public void get(GetRequest request, StreamObserver<Employee> responseObserver) {
//            String id = request.getId();
//            Employee employee = employeeMap.get(id);
//
//            if (employee != null) {
//                responseObserver.onNext(employee);
//                responseObserver.onCompleted();
//            } else {
//                responseObserver.onError(new RuntimeException("Employee not found with id: " + id));
//            }
//        }
//
//        private String generateId() {
//            return String.valueOf(employeeMap.size() + 1);
//        }
//    }
//}
