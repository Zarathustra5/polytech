`timescale 1ns/1ns
module tb_mux2_1 #(parameter w=4) ();

reg sel, clk;
reg [w:1]dA, dB;
reg [w:1]muxOUT;

mux2_1 #(.w(w)) dut (.dA(dA), .dB(dB), .sel(sel), .muxOUT(muxOUT));

initial begin: clock_generator
	clk = 1'b0; forever #10 clk = ~clk;
end

initial begin: stim
	dA = 0;
	dB = 1;
	sel = 0;
	#10 sel = 1;
	#10 sel = 0;
	#10 sel = 1;
end

initial begin: show
	$display("\t\t	Time	dA	dB	sel	muxOUT");
	$monitor($time,,,,,dA,,,,,dB,,,,,sel,,,,,muxOUT);
	#360 $stop;
end

endmodule