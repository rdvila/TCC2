library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="all.png", width=750, height=300)
a <- read.csv(file='all.txt', head=FALSE, sep=",")
a$V10 <- as.factor(a$V10)
ggplot(data=a, aes(x=V10, y=V8, colour=V1, group=V1, linetype=V1)) + geom_line() + geom_point() + xlab("Fator") + ylab("Ocupação")  + scale_linetype_discrete("Dados") + scale_colour_discrete("Dados") + ggtitle("Teste de fator de crossover")
dev.off()