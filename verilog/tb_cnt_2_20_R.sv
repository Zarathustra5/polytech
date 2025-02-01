`timescale 1ns/1ns
module tb_cnt_2_10_R();

reg clk, dir, reset;
reg [3:0]cntQ;

cnt_2_10_R dut (.clk(clk), .dir(dir), .reset(reset), .cntQ(cntQ));

initial begin: counter_generate
	clk = 1'b0; forever #10 clk = ~clk;
end

initial begin: stim
	dir = 0;
	reset = 0;
	cntQ = 4'b0000;
	#10 reset = 1;
	#20 reset = 0;
	#150 reset = 1;
	#20 reset = 0;
	#100 dir = 1;
	//#100 dir = 0;
end

initial begin: show
	$display("\t\t Time	dir	reset		cntQ");
	$monitor($time,,,,,dir,,,,,reset,,,,,cntQ);
	repeat (33) @(posedge clk);
	$stop;
end
endmodule