## 2.4 Linear Stability Analysis

**Use linear stability analysis to classify the fixed points of the following systems. If linear stability analysis fails because $f'(x^*) = 0$, use a graphical argument to decide the stability.**

**2.4.1** $\dot{x} = x(1-x)$
**2.4.2** $\dot{x} = x(1-x)(2-x)$
**2.4.3** $\dot{x} = \tan(x)$
**2.4.4** $\dot{x} = x^2(6-x)$
**2.4.5** $\dot{x} = 1 - e^{-x^2}$
**2.4.6** $\dot{x} = \ln(x)$
**2.4.7** $\dot{x} = ax - x^3$, where $a$ can be positive, negative, or zero. Discuss all three cases.

**2.4.8** 
Using linear stability analysis, classify the fixed points of the Gompertz model of tumor growth $\dot{N} = -aN \ln(bN)$.

**2.4.9 (Critical slowing down)** 
In statistical mechanics, "critical slowing down" is a signature of a second-order phase transition. The system relaxes to equilibrium much more slowly than usual. 
a) Obtain the analytical solution to $\dot{x} = -x^3$ for an arbitrary initial condition. Show that $x(t) \to 0$ as $t \to \infty$ but that the decay is not exponential. *(You should find that the decay is a much slower algebraic function of $t$.)*
b) To get some intuition about the slowness, sketch a numerically accurate plot of the solution for $x_0 = 10$, for $0 \le t \le 10$. On the same graph, plot the solution to $\dot{x} = -x$ for the same initial condition.

---

## 2.5 Existence and Uniqueness

**2.5.1 (Reaching a fixed point in a finite time)** 
A particle travels on the half-line $x \ge 0$ with a velocity given by $\dot{x} = -xc$, where $c$ is a real constant.
a) Find all values of $c$ such that the origin $x=0$ is a stable fixed point.
b) Now assume that $c$ is chosen such that $x=0$ is stable. Can the particle ever reach the origin in a finite time? Specifically, how long does it take for the particle to travel from $x=1$ to $x=0$, as a function of $c$?

**2.5.2 ("Blow-up": Reaching infinity in a finite time)** 
Show that the solution to $\dot{x} = 1 + x^{10}$ escapes to infinity in a finite time, starting from any initial condition. *(Hint: Don’t try to find an exact solution; instead, compare the solutions to those of $\dot{x} = 1 + x^2$.)*

**2.5.3** 
Consider the equation $\dot{x} = rx + x^3$, where $r > 0$ is fixed. Show that $x(t) \to \infty$ in finite time, starting from any initial condition $x_0 > 0$.

**2.5.4 (Infinitely many solutions with the same initial condition)** 
Show that the initial value problem $\dot{x} = x^{1/3}, \, x(0) = 0$ has an infinite number of solutions. *(Hint: Construct a solution that stays at $x=0$ until some arbitrary time $t_0$, after which it takes off.)*

**2.5.5 (A general example of non-uniqueness)** 
Consider the initial value problem $\dot{x} = |x|^{p/q}, \, x(0) = 0$ where $p$ and $q$ are positive integers with no common factors.
a) Show that there are an infinite number of solutions if $p < q$.
b) Show that there is a unique solution if $p \ge q$.

**2.5.6 (The leaky bucket)** 
Consider a water bucket with a hole in the bottom. If you see an empty bucket with a puddle beneath it, you cannot figure out when the bucket was full. The solution to the corresponding differential equation must be non-unique when integrated backwards in time.
Let $h(t) =$ height of water remaining; $a =$ area of the hole; $A =$ cross-sectional area of bucket; $v(t) =$ velocity of water escaping.
a) Show that $av(t) = -A\dot{h}(t)$. What physical law are you invoking?
b) Using conservation of energy, derive $v^2 = 2gh$.
c) Combining (a) and (b), show $\dot{h} = -C\sqrt{h}$, where $C = \frac{a}{A}\sqrt{2g}$.
d) Given $h(0) = 0$ (bucket empty at $t=0$), show that the solution for $h(t)$ is non-unique in backwards time ($t < 0$).

---

## 2.6 Impossibility of Oscillations

**2.6.1** 
Explain this paradox: a simple harmonic oscillator $m\ddot{x} = -kx$ is a system that oscillates in one dimension (along the x-axis). But the text says one-dimensional systems can’t oscillate.

**2.6.2 (No periodic solutions to $\dot{x} = f(x)$)** 
Here’s an analytic proof that periodic solutions are impossible for a vector field on a line. Suppose on the contrary that $x(t)$ is a nontrivial periodic solution, i.e., $x(t) = x(t + T)$ for some $T > 0$, and $x(t) \neq x(t + s)$ for all $0 < s < T$. Derive a contradiction by considering the integral $\int_t^{t+T} f(x) \frac{dx}{dt} dt$.

---

## 2.7 Potentials

**For each of the following vector fields, plot the potential function $V(x)$ and identify all the equilibrium points and their stability.**

**2.7.1** $\dot{x} = x(1-x)$
**2.7.2** $\dot{x} = 3$
**2.7.3** $\dot{x} = \sin(x)$
**2.7.4** $\dot{x} = 2 + \sin(x)$
**2.7.5** $\dot{x} = -\sinh(x)$
**2.7.6** $\dot{x} = r + x - x^3$, for various values of $r$.

**2.7.7 (Another proof that solutions to $\dot{x} = f(x)$ can’t oscillate)** 
Let $\dot{x} = f(x)$ be a vector field on the line. Use the existence of a potential function $V(x)$ to show that solutions $x(t)$ cannot oscillate.

---

## 2.8 Solving Equations on the Computer

**2.8.1 (Slope field)** 
The slope is constant along horizontal lines in a slope field for autonomous equations. Why should we have expected this?

**2.8.2** 
Sketch the slope field for the following differential equations. Then "integrate" the equation manually by drawing trajectories that are everywhere parallel to the local slope.
a) $\dot{x} = x$
b) $\dot{x} = 1 - x^2$
c) $\dot{x} = 1 - 4x(1-x)$
d) $\dot{x} = \sin(x)$

**2.8.3 (Calibrating the Euler method)** 
The goal of this problem is to test the Euler method on the initial value problem $\dot{x} = -x, \, x(0) = 1$.
a) Solve the problem analytically. What is the exact value of $x(1)$?
b) Using the Euler method with step size $\Delta t = 1$, estimate $x(1)$ numerically—call the result $\hat{x}(1)$. Then repeat, using $\Delta t = 10^{-n}$, for $n = 1, 2, 3, 4$.
c) Plot the error $E = |x(1) - \hat{x}(1)|$ as a function of $\Delta t$. Then plot $\ln(E)$ vs. $\ln(\Delta t)$. Explain the results.

**2.8.4** Redo Exercise 2.8.3, using the improved Euler method.
**2.8.5** Redo Exercise 2.8.3, using the Runge-Kutta method.

**2.8.6 (Analytically intractable problem)** 
Consider the initial value problem $\dot{x} = x + e^{-x}, \, x(0) = 0$. This problem can’t be solved analytically.
a) Sketch the solution $x(t)$ for $t \ge 0$.
b) Using analytical arguments, obtain rigorous bounds on the value of $x$ at $t=1$. Prove that $a \le x(1) \le b$. Try to make $a$ and $b$ as close as possible. *(Hint: Bound the given vector field by approximate vector fields that can be integrated analytically.)*
c) Using the Euler method, compute $x$ at $t=1$, correct to three decimal places. How small does the step size need to be to obtain the desired accuracy?
d) Repeat part (c), now using the Runge-Kutta method. Compare the results for step sizes $\Delta t = 1, \, 0.1, \, 0.01$.

**2.8.7 (Error estimate for Euler method)** 
Use Taylor series expansions to estimate the error in taking one step by the Euler method. Compare the exact value $x(t_0 + \Delta t)$ with the Euler approximation $x_0 + f(x_0)\Delta t$. Show that the local error is $O(\Delta t^2)$.

**2.8.8 (Error estimate for the improved Euler method)** 
Use Taylor series arguments to show that the local error for the improved Euler method is $O(\Delta t^3)$.

**2.8.9 (Error estimate for Runge-Kutta)** 
Show that the Runge-Kutta method produces a local error of size $O(\Delta t^5)$. *(Warning: This calculation involves massive amounts of algebra. Teach yourself a symbolic manipulation language and do the problem on the computer.)*
