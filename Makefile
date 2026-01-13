all: src/*.java
	mkdir -p bin && \
	javac -sourcepath src -d bin src/Main.java

clean:
	rm -rf bin/*
