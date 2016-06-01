bv1 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Battery/HyperPipes.txt", 
                stringsAsFactors = FALSE) 
bv2 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Battery/NaiveBayes.txt", 
                stringsAsFactors = FALSE) 
bv3<- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Battery/RandomForest.txt", 
               stringsAsFactors = FALSE) 


hp <- bv1[, 3] 
nb <- bv2[, 3] 
rf <- bv3[, 3] 

plot(hp, type = "l", col = "red", xaxt = "n", xlab = "minutes", ylab = "Battery level") 
lines(nb, col = "green") 
lines(rf, col = "purple") 

axis(1, at = seq(1, 101, 2), labels = 1:51)  
legend("topright", c("HyperPipes", "Naive Bayes", "Random Forest"), 
       lty = c(1, 1, 1), lwd = c(1, 1, 1), col=c("red", "green", "purple"))  
 